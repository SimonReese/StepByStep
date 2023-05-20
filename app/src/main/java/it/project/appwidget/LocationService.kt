package it.project.appwidget

import android.Manifest
import android.app.NotificationChannel
import android.app.NotificationManager
import android.app.Service
import android.content.Intent
import android.content.pm.PackageManager
import android.location.Location
import android.location.LocationListener
import android.location.LocationManager
import android.location.LocationRequest
import android.os.IBinder
import android.util.Log
import androidx.core.app.NotificationCompat

/**
 * Servizio foreground per localizzazione.
 *
 * Questo servizio utilizza LocationManager per ricevere aggiornamenti sulla posizione
 * del dispositivo. Rimarrà in esecuzione anche quando l'applicazione è chiusa, tramite una notifica
 * che compare al momento dell'avvio del servizio.
 *
 */
class LocationService : Service() {

    // Variabili per gestione della notifica del serizio
    private lateinit var notificationManager: NotificationManager
    private lateinit var notificationBuilder: NotificationCompat.Builder
    companion object {
        // Id notifica
        const val SERVICE_NOTIFICATION_ID: Int = 1  //TODO: spostare valore in uno dei file xml?
        const val NOTIFICATION_CHANNEL_ID: String = "LocationServiceChannel"
        const val NOTIFICATION_CHANNEL_DESCRIPTION: String = "Canale per notifiche servizio localizzazione" //TODO: Spostare in strings.xml
    }

    // Variabili per la localizzazione
    private lateinit var locationManager: LocationManager
    private lateinit var locationListener: LocationListener
    private val minLocationUpdateIntervalMs: Long = 0
    private var minLocationUpdateDistanceM: Float = 0F
    private var minAccuracy: Float = 20F
    private var minSum: Float = 100F
    private lateinit var lastRelevantLocation: Location
    private var sumDistance: Float = 0F
    //Calcola tempo totale sessione
    private lateinit var firstRelevantLocation: Location
    // Vettore per il salvataggio delle location
    private var locationList: ArrayList<Location> = ArrayList()

    // Classe privata per gestire aggionamenti della posizione
    private inner class CustomLocationListener: LocationListener {
        override fun onLocationChanged(location: Location) {
            Log.d("LocationService", "latitudine: ${location.latitude}, longitudine: ${location.longitude}, " +
                    "velocità: ${location.speed}(+- ${location.speedAccuracyMetersPerSecond}), " +
                    "accuratezza: ${location.accuracy}")

            //TODO: Revisionare strategie localizzazione https://stuff.mit.edu/afs/sipb/project/android/docs/guide/topics/location/strategies.html

            // Controllo che la notifica sia già impostata
            if (this@LocationService::notificationBuilder.isInitialized && this@LocationService::notificationManager.isInitialized){
                // Aggiorno valori sulla notifica
                notificationBuilder.setContentText("Latitudine: ${location.latitude}, Longitudine: ${location.longitude}")
                // Visualizzo aggiornamenti notifica
                notificationManager.notify(SERVICE_NOTIFICATION_ID, notificationBuilder.build())
            }



            // Filtro locations inaccurate
            if (location.accuracy >= minAccuracy){
                return
            }

            // Salvo posizione rilevante, se non è mai stata salvata
            if (!this@LocationService::lastRelevantLocation.isInitialized){
                lastRelevantLocation = location
                firstRelevantLocation = location // TODO: non serve più, basta prendere il primo elemento
                locationList.add(location)
            }
            // Altrimenti, se la distanza di questa rispetto all'ultima rilevante è maggiore di <minSum> metri, aggiorniamo la distanza e aggiorniamo la somma
            else if (lastRelevantLocation.distanceTo(location) >= minSum){
                sumDistance += lastRelevantLocation.distanceTo(location)
                lastRelevantLocation = location
                locationList.add(location)
            }

            // Aggiorno il testo del widget
            NewAppWidget().updateLocationText(this@LocationService, location.latitude, location.longitude, sumDistance)

            // Invio broadcast
            val intent = Intent("location-update")
            intent.putExtra("speed", location.speed)
            intent.putExtra("accuracy", location.accuracy)
            intent.putExtra("distanza", sumDistance)
            sendBroadcast(intent)

            Log.d("onLocationChanged","sumDistance: ${sumDistance}\"")
            Log.d("onLocationChanged","Tempo trascorso: ${System.currentTimeMillis() - lastRelevantLocation.time}\"")


            // +++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++
            // +++++++++++++++++++++++++++++++++++++ SPOSTARE IN UN WORKERMANAGER ASINCRONO ALLA CHIUSURA DEL SERVICE ++++++++++++++++++++
            // +++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++
            // Spostare il carico in un workermanager alla chiusura del servizio. Memorizzare tutta la sessione in un vettore e alla fine calcolare tutto da workermanager
            // passando il vettore
            // TODO: creare il vettore


            //TODO: rivedere i valori degli if e else if che definiscono reset e salvataggio attività
            //Controllo se non ho iniziato una sessione ovvero se non ho camminato per almeno 100 metri negli ultimi 10 minuti
            if(sumDistance <= 100 && (System.currentTimeMillis() - lastRelevantLocation.time) >= 600000)
            {
                // Resetto i valori per creare una nuova sessione
                Log.d("onLocationChanged","Sessione resettata")

                sumDistance = 0F
                lastRelevantLocation = location
            }
            // Controllo se la sessione deve essere salvata: devo aver fatto più di 100 metri e l'ultima location
            // rilevante è stata aggiornata l'ultima volta più di xxxx fa (in questo caso 12 secondi per il testing)
            else if (sumDistance >= 100 && (System.currentTimeMillis() - lastRelevantLocation.time) >= 12000) {
                val endTime = System.currentTimeMillis()
                var duration = endTime - firstRelevantLocation.time


                // Calcolo i valori mancanti utilizzando SessionDataProcessor
                val activityType = SessionDataProcessor.calculateActivityType(sumDistance, duration)
                val averageSpeed = SessionDataProcessor.calculateAverageSpeed(sumDistance, duration)
                val maxSpeed = SessionDataProcessor.calculateMaxSpeed(location.speed)

                Log.d("onLocationChanged","Salvataggio sessione nel database")
                // Creo una nuova sessione con i dati calcolati
                val trackSession = TrackSession(
                    startTime = firstRelevantLocation.time,
                    endTime = endTime,
                    duration = duration,
                    distance = sumDistance.toDouble(),
                    averageSpeed = averageSpeed,
                    maxSpeed = maxSpeed,
                    activityType = activityType
                )

                // Salvo la sessione nel database utilizzando trackSessionDao
                val trackSessionDao = AppDatabase.getInstance(this@LocationService).trackSessionDao()
                trackSessionDao.insertSession(trackSession)
                Log.d("onLocationChanged","Sessione salvata nel database")

                // Resetto i valori per creare una nuova sessione
                sumDistance = 0F
                lastRelevantLocation = location
                firstRelevantLocation = location
            }


            // +++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++
            // +++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++




        }
    }

    override fun onCreate() {
        super.onCreate()
        // Istanziazione variabili
        locationManager = getSystemService(LOCATION_SERVICE) as LocationManager
        locationListener = CustomLocationListener()
        notificationManager = getSystemService(NOTIFICATION_SERVICE) as NotificationManager

        // Creo canale per le notifiche
        val channel = NotificationChannel(NOTIFICATION_CHANNEL_ID, NOTIFICATION_CHANNEL_DESCRIPTION, NotificationManager.IMPORTANCE_DEFAULT)
        notificationManager.createNotificationChannel(channel)
        Log.d("LocationService", "Servizio creato (onCreate)")
    }

    override fun onStartCommand(intent: Intent?, flags: Int, startId: Int): Int {
        // Impostazioni notifica
        notificationBuilder = NotificationCompat.Builder(this, NOTIFICATION_CHANNEL_ID).apply {
            setSmallIcon(R.drawable.ic_launcher_foreground) //TODO: cambiare icona
            setContentTitle("Servizio di localizzazione")   //Titolo notifica
            setContentText("Servizio di localizzazione in esecuzione")  //Descrizione notifica
            setPriority(NotificationCompat.PRIORITY_DEFAULT)    //Priorità notifica standard
            setForegroundServiceBehavior(NotificationCompat.FOREGROUND_SERVICE_IMMEDIATE)   //La notifica viene impostata immediatamente
            setOnlyAlertOnce(true) //Se la notifica viene aggiornata, solo la prima volta emette suono
        }

        // Controllo permessi
        if (checkSelfPermission(Manifest.permission.ACCESS_COARSE_LOCATION) == PackageManager.PERMISSION_DENIED){
            throw Error("Permesso non disponibile") //TODO: gestire il caso di assenza dei permessi
        }
        if (checkSelfPermission(Manifest.permission.ACCESS_FINE_LOCATION) == PackageManager.PERMISSION_DENIED){
            throw Error("Permesso non disponibile") //TODO: gestire il caso di assenza dei permessi
        }

        // RICEVO PARAMETRI DI DEBUG
        minLocationUpdateDistanceM = intent?.getFloatExtra("minDistance", 0F)!!
        minAccuracy = intent?.getFloatExtra("minAccuracy", 20F)!!
        minSum = intent?.getFloatExtra("minSum", 100F)!!

        // Imposto listener
        //locationManager.requestLocationUpdates(LocationManager.FUSED_PROVIDER,
            //minLocationUpdateIntervalMs, minLocationUpdateDistanceM, locationListener)
        val locationRequest:LocationRequest = LocationRequest.Builder(minLocationUpdateIntervalMs).apply {
            setQuality(LocationRequest.QUALITY_HIGH_ACCURACY)
            setMinUpdateDistanceMeters(minLocationUpdateDistanceM)
        }.build()
        locationManager.requestLocationUpdates(LocationManager.FUSED_PROVIDER, locationRequest, mainExecutor, locationListener)

        // Invio notifica e avvio del servizio in foreground
        startForeground(SERVICE_NOTIFICATION_ID, notificationBuilder.build())

        Log.d("LocationService", "Servizio avviato (onStartCommand) con parametri: $minLocationUpdateDistanceM, $minAccuracy, $minSum")

        // Imposto servizio come NON_STICKY (non si riavvia allo stop)
        return START_NOT_STICKY
    }

    override fun onBind(intent: Intent): IBinder {
        TODO("Return the communication channel to the service.")
        Log.d("LocationService", "Servizio collegato (onBind)")
    }

    override fun onDestroy() {
        super.onDestroy()
        // Fermo aggiornamenti
        locationManager.removeUpdates(locationListener)
        // Rimuovo notifica
        stopForeground(STOP_FOREGROUND_REMOVE)
        Log.d("LocationService", "Servizio distrutto (onDestroy)")
        stopSelf()
    }

}
