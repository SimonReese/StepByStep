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
    private val minLocationUpdateDistanceM: Float = 10F

    // Classe privata per gestire aggionamenti della posizione
    private inner class CustomLocationListener: LocationListener {
        override fun onLocationChanged(location: Location) {
            Log.d("LocationService", "latitudine: ${location.latitude}, longitudine: ${location.longitude}, velocità: ${location.speed}")

            // Aggiorno il testo del widget
            NewAppWidget().updateLocationText(this@LocationService, location.latitude, location.longitude)

            // Controllo che la notifica sia già impostata
            if (this@LocationService::notificationBuilder.isInitialized && this@LocationService::notificationManager.isInitialized){
                // Aggiorno valori sulla notifica
                notificationBuilder.setContentText("Latitudine: ${location.latitude}, Longitudine: ${location.longitude}")
                // Visualizzo aggiornamenti notifica
                notificationManager.notify(SERVICE_NOTIFICATION_ID, notificationBuilder.build())
            }

            // Invio broadcast
            val intent = Intent("location-update")
            intent.putExtra("speed", location.speed)
            sendBroadcast(intent)
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

        // Imposto listener
        locationManager.requestLocationUpdates(LocationManager.FUSED_PROVIDER,
            minLocationUpdateIntervalMs, minLocationUpdateDistanceM, locationListener)

        // Invio notifica e avvio del servizio in foreground
        startForeground(SERVICE_NOTIFICATION_ID, notificationBuilder.build())

        Log.d("LocationService", "Servizio avviato (onStartCommand)")

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
    }

}
