package it.project.appwidget

import android.Manifest
import android.content.Intent
import android.content.pm.PackageManager
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.util.Log
import android.widget.Button
import android.widget.TextView

class ServiceMonitorActivity : AppCompatActivity() {

    // Variabile per gestione dei permessi
    private var hasPermissions: Boolean = false //TODO: riprogettare la gestione dei permessi
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_service_monitor)

        val statusTextView: TextView = findViewById(R.id.serviceStatusTextView)
        val startServiceButton: Button = findViewById(R.id.startServiceButton)
        val stopServiceButton: Button = findViewById(R.id.stopServiceButton)

        // Controllo permessi
        hasPermissions = true // Devo supporla vera, perchè non è detto che onRequestPermissionsResult() sia stato chiamato
        if (checkSelfPermission(Manifest.permission.POST_NOTIFICATIONS) == PackageManager.PERMISSION_DENIED){
            //TODO: analizzare permesso POST_NOTIFICATION (pare che sia introdotto da android 13, cosa fare nel 12)?
            hasPermissions = false
            Log.w("ServiceMonitorActivity", "Permesso {POST_NOTIFICATIONS} non concesso")
        }
        if (checkSelfPermission(Manifest.permission.ACCESS_COARSE_LOCATION) == PackageManager.PERMISSION_DENIED){
            hasPermissions = false
            Log.w("ServiceMonitorActivity", "Permesso {ACCESS_COARSE_LOCATION} non concesso")
        }
        if (checkSelfPermission(Manifest.permission.ACCESS_FINE_LOCATION) == PackageManager.PERMISSION_DENIED){
            hasPermissions = false
            Log.w("ServiceMonitorActivity", "Permesso {ACCESS_FINE_LOCATION} non concesso")
        }

        if (!hasPermissions){
            // Chiedo tutti i permessi un una volta sola
            //TODO testare cosa succede se un permesso viene negato dalle impostazioni (i permessi concessi vengono chiesti nuovamente?)
            requestPermissions(arrayOf(
                Manifest.permission.POST_NOTIFICATIONS,
                Manifest.permission.ACCESS_COARSE_LOCATION,
                Manifest.permission.ACCESS_FINE_LOCATION), 1)
            Log.d("ServiceMonitorActivity", "Non sono stati concessi tutti i permessi necessari")
            statusTextView.text = "Impossibile utilizzare il servizio\nNon sono stati forniti tutti i permessi necessari"
            return
        }

        // Creo intent per il servizio
        val serviceIntent = Intent(this, LocationService::class.java)

        // Listener per avviare il servizio
        startServiceButton.setOnClickListener{
            startForegroundService(serviceIntent)
        }

        // Listener per fermare il servizio
        stopServiceButton.setOnClickListener{
            stopService(serviceIntent)
        }

    }

    override fun onRequestPermissionsResult(
        requestCode: Int,
        permissions: Array<out String>,
        grantResults: IntArray
    ) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults)
        // Scansiono tutti i risultati
        for (result in grantResults){
            // Se il permesso è stato negato dall'utente, non posso fare altro
            if (result == PackageManager.PERMISSION_DENIED){
                hasPermissions = false
                return
            }
        }
        // Se tutti i permessi sono stati concessi, posso resettare l'activity e usare i permessi
        hasPermissions = true
        recreate() //TODO: c'è un modo migliore per gestire questo workflow?
    }
}