package it.project.appwidget

import android.Manifest
import android.app.NotificationChannel
import android.app.NotificationManager
import android.app.PendingIntent
import android.app.Service
import android.content.ComponentName
import android.content.Intent
import android.content.pm.PackageManager
import android.location.Location
import android.location.LocationListener
import android.location.LocationManager
import android.location.LocationRequest
import android.os.IBinder
import android.util.Log
import androidx.core.app.NotificationCompat
import androidx.work.Data
import androidx.work.OneTimeWorkRequestBuilder
import androidx.work.WorkManager
import androidx.work.WorkRequest
import it.project.appwidget.activities.MainActivity
import it.project.appwidget.util.LocationParser
import it.project.appwidget.widgets.NewAppWidget

// TODO: Valutare se spostare tutto il lavoro del Service in un thread separato (non service in background - service sempre in Foreground ma non su mainThread)

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

    private lateinit var locationManager: LocationManager
    private lateinit var locationListener: LocationListener

    // Parametri per la localizzazione
    private val minLocationUpdateIntervalMs: Long = 0
    private var minLocationUpdateDistanceM: Float = 0F
    private var minAccuracy: Float = 20F
    private var minSum: Float = 10F

    // Parametri sessione
    private val kcal_to_m_to_kg_factor: Float = 0.001f //kcal consumate per ogni metro per ogni chilo
    private val weight: Float = 70f // Peso in kg di riferimento

    private var sumDistance: Float = 0F
    private var locationList: ArrayList<Location> = ArrayList() // Vettore per il salvataggio delle location

    // Classe privata per gestire aggionamenti della posizione
    private inner class CustomLocationListener: LocationListener {
        override fun onLocationChanged(currentLocation: Location) {
            Log.d("CustomLocationListener", "latitudine: ${currentLocation.latitude}, longitudine: ${currentLocation.longitude}, " +
                    "velocità: ${currentLocation.speed}(+- ${currentLocation.speedAccuracyMetersPerSecond}), " +
                    "accuratezza: ${currentLocation.accuracy}")

            //TODO: Revisionare strategie localizzazione https://stuff.mit.edu/afs/sipb/project/android/docs/guide/topics/location/strategies.html

           // Controllo che la notifica sia già impostata, e la aggiorno con le nuove coordinate
            if (this@LocationService::notificationBuilder.isInitialized && this@LocationService::notificationManager.isInitialized){
                // Aggiorno valori sulla notifica
                notificationBuilder.setContentText("Latitudine: ${currentLocation.latitude}, Longitudine: ${currentLocation.longitude}")
                // Visualizzo aggiornamenti notifica
                notificationManager.notify(SERVICE_NOTIFICATION_ID, notificationBuilder.build())
            }

            // Filtro locations inaccurate
            if (currentLocation.accuracy >= minAccuracy){
                return
            }

            // Salvo posizione se non è mai stata salvata
            if (locationList.size == 0){
                locationList.add(currentLocation)
            }
            // Altrimenti, se la distanza di questa rispetto all'ultima posizione salvata è maggiore di <minSum> metri, aggiorniamo la distanza e aggiorniamo la somma
            else if (currentLocation.distanceTo(locationList.last()) >= minSum){
                sumDistance += currentLocation.distanceTo(locationList.last())
                locationList.add(currentLocation)
            }

            // Calcolo il rate: tempo (in minuti) necessario a percorrere 1 km
            var rate = 0.00f
            // Considero solo velocità superiori a 0.5 m/s
            if (currentLocation.speed > 0.5){
                rate = (1000 / currentLocation.speed) / 60
            }

            // Calcolo calorico
            var calories = 0f
            if (sumDistance > 0){
                calories = kcal_to_m_to_kg_factor * sumDistance * weight
            }

            /* Invio broadcasts.
            Affinchè il widget riceva il broadcast, è necessario inviare un intent ESPLICITO. Tuttavia
            è necessario inviare il broadcast anche al fragment. Creiamo quindi due intent.*/

            // Creo intent implicito generico
            val implicitIntent = Intent("location-update")
            // Valori recuperati
            implicitIntent.putExtra("latitude", currentLocation.latitude)
            implicitIntent.putExtra("longitude", currentLocation.longitude)
            implicitIntent.putExtra("accuracy", currentLocation.accuracy)
            implicitIntent.putExtra("speed", currentLocation.speed)
            // Valori calcolati
            implicitIntent.putExtra("distance", sumDistance)
            implicitIntent.putExtra("rate", rate)
            implicitIntent.putExtra("calories", calories)
            // Valori costanti
            implicitIntent.putExtra("startTime", locationList[0].time) // E' il tempo rispetto alla Unix Epoch
            implicitIntent.putExtra("startTime_elapsedRealtimeNanos", locationList[0].elapsedRealtimeNanos) // E' il tempo trascorso rispetto al boot di sistema

            // Copio intent generico e creo intent esplicito
            val explicitIntent = Intent(implicitIntent)
            explicitIntent.component = ComponentName(this@LocationService, NewAppWidget::class.java)
            // Invio intents
            sendBroadcast(implicitIntent)
            sendBroadcast(explicitIntent)
            // TODO: Broadcast o LiveData?

            Log.d("CustomLocationListener","Inviato messaggio broadcast con: " +
                    "[long: ${currentLocation.longitude}, lat: ${currentLocation.latitude}, acc: ${currentLocation.accuracy}, " +
                    "speed: ${currentLocation.speed}, dist: ${sumDistance},]")
            Log.d("CustomLocationListener","Tempo trascorso: ${System.currentTimeMillis() - locationList.last().time}\"")


            if(System.currentTimeMillis() - locationList.last().time >= 600000) {
                // Resetto i valori per creare una nuova sessione
                Log.d("CustomLocationListener", "Sessione resettata")
                //TODO: Mandare broadcast a Run per fermare timer e disattivare bottone?
                //Oppure togliamo tutto questo codice?
                stopSelf()
            }

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
        val runFragmentIntent = Intent(this, MainActivity::class.java).apply{
            this.flags = Intent.FLAG_ACTIVITY_NEW_TASK or Intent.FLAG_ACTIVITY_CLEAR_TASK
            putExtra("fromService", true)
        }
        val pendingIntent = PendingIntent.getActivity(this, 0, runFragmentIntent, PendingIntent.FLAG_IMMUTABLE)
        // Impostazioni notifica
        notificationBuilder = NotificationCompat.Builder(this, NOTIFICATION_CHANNEL_ID).apply {
            setSmallIcon(R.drawable.ic_launcher_foreground) //TODO: cambiare icona
            setContentTitle("Servizio di localizzazione")   //Titolo notifica
            setContentText("Servizio di localizzazione in esecuzione")  //Descrizione notifica
            priority = NotificationCompat.PRIORITY_DEFAULT    //Priorità notifica standard
            foregroundServiceBehavior = NotificationCompat.FOREGROUND_SERVICE_IMMEDIATE   //La notifica viene impostata immediatamente
            setOnlyAlertOnce(true) //Se la notifica viene aggiornata, solo la prima volta emette suono
            setContentIntent(pendingIntent)
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
        minAccuracy = intent.getFloatExtra("minAccuracy", 30F)
        minSum = intent.getFloatExtra("minSum", 100F)

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

    override fun onBind(intent: Intent): IBinder? {
        // TODO: restituire null se bind non supportata.
        // TODO("Return the communication channel to the service.")
        Log.d("LocationService", "Servizio collegato (onBind)")
        return null
    }

    override fun onDestroy() {
        // Viene chiamato da Context.stopService() o Service.stopSelf()

        // Fermo aggiornamenti
        locationManager.removeUpdates(locationListener)
        // TODO: rivedeve bene come fermare un servizio

        // Converto locations in stringhe
        val locationListString = Array(locationList.size) { "" }
        for ((position, location) in locationList.withIndex()){
            // Effettuo parsing della location
            val stringLocation = LocationParser.toString(location)
            // Aggiungo stringa alla lista stringhe
            locationListString.set(position, stringLocation)
        }

        // Creo oggetto Data da inviare al worker
        val data = Data.Builder().putStringArray("locationListString", locationListString).build()

        // Avvio work per elaborazione passando dati in input
        val sessionWorkerRequest: WorkRequest = OneTimeWorkRequestBuilder<TrackSessionWorker>().setInputData(data).build()
        WorkManager.getInstance(applicationContext).enqueue(sessionWorkerRequest)

        /* Rimuovo notifica - secondo la documentazione ufficiale di Android, se il servizio viene fermato
         * mentre è in esecuzione in foreground, la sua notifica viene rimossa automaticamente.
         * https://developer.android.com/guide/components/foreground-services#remove-from-foreground.
         */
        // stopForeground(STOP_FOREGROUND_REMOVE)

        Log.d("LocationService", "Servizio distrutto (onDestroy)")
        super.onDestroy()
    }

}
