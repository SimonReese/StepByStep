package it.project.appwidget

import android.Manifest
import android.content.BroadcastReceiver
import android.content.Context
import android.content.Intent
import android.content.IntentFilter
import android.content.pm.PackageManager
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.os.PersistableBundle
import android.util.Log
import android.widget.Button
import android.widget.SeekBar
import android.widget.TextView

class ServiceMonitorActivity : AppCompatActivity() {

    // Variabili views per salvataggio stato
    // Informazioni posizione
    private lateinit var statusTextView: TextView
    private lateinit var accuracyTextView: TextView
    private lateinit var distanceTextView: TextView

    // DEBUG - parametri servizio
    private lateinit var minDistSeekBar: SeekBar
    private lateinit var minAccSeekBar: SeekBar
    private lateinit var minSumSeekBar: SeekBar

    private lateinit var minDistTextView: TextView
    private lateinit var minAccTextView: TextView
    private lateinit var minSumTextView: TextView

    // Variabile per gestione dei permessi
    private var hasPermissions: Boolean = false //TODO: riprogettare la gestione dei permessi

    private lateinit var locationBroadcastReceiver: LocationBroadcastReceiver

    // Classe per ricezione broadcast messages
    private inner class LocationBroadcastReceiver(val speed: TextView, val accuracy: TextView, val distance: TextView): BroadcastReceiver() {
        override fun onReceive(context: Context?, intent: Intent?) {
            val speedloc = intent?.getFloatExtra("speed", 0f)
            val accloc = intent?.getFloatExtra("accuracy", 0f)
            val distloc = intent?.getFloatExtra("distanza", 0f)
            speed.text = speedloc.toString()
            accuracy.text = accloc.toString()
            distance.text = distloc.toString()
        }

    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_service_monitor)

        statusTextView = findViewById(R.id.serviceStatusTextView)
        accuracyTextView = findViewById(R.id.accuracyTextView)
        distanceTextView = findViewById(R.id.distanceTextView)
        val startServiceButton: Button = findViewById(R.id.startServiceButton)
        val stopServiceButton: Button = findViewById(R.id.stopServiceButton)

        // Creo intent per il LocationService
        val serviceIntent = Intent(this, LocationService::class.java)

        // VIEW DI DEBUG -------------------------------------------------------
        minDistSeekBar = findViewById(R.id.minDistSeekBar)
        minDistTextView = findViewById(R.id.minDistTextView)
        var minDistance: Float = (minDistSeekBar.progress * 10).toFloat()
        minDistTextView.text = minDistance.toString() + "m"

        minAccSeekBar = findViewById(R.id.minAccSeekBar)
        minAccTextView = findViewById(R.id.minAccTextView)
        var minAccuracy : Float = (minAccSeekBar.progress * 10).toFloat()
        minAccTextView.text = minAccuracy.toString() + "m"

        minSumSeekBar = findViewById(R.id.minSumSeekBar)
        minSumTextView = findViewById(R.id.minSumTextView)
        var minSum : Float = (minSumSeekBar.progress * 10).toFloat()
        minSumTextView.text = minSum.toString() + "m"

        // Listener per minDistSeekBar
        minDistSeekBar.setOnSeekBarChangeListener(object : SeekBar.OnSeekBarChangeListener {
            override fun onProgressChanged(seekBar: SeekBar?, progress: Int, fromUser: Boolean) {
                minDistance = (minDistSeekBar.progress * 10).toFloat()
                minDistTextView.text = minDistance.toString() + "m"
            }

            override fun onStartTrackingTouch(seekBar: SeekBar?) {
                minDistance = (minDistSeekBar.progress * 10).toFloat()
                minDistTextView.text = minDistance.toString() + "m"
            }

            override fun onStopTrackingTouch(seekBar: SeekBar?) {
                minDistance = (minDistSeekBar.progress * 10).toFloat()
                minDistTextView.text = minDistance.toString() + "m"
            }
        })

        // Listener per minAccSeekBar
        minAccSeekBar.setOnSeekBarChangeListener(object : SeekBar.OnSeekBarChangeListener {
            override fun onProgressChanged(seekBar: SeekBar?, progress: Int, fromUser: Boolean) {
                minAccuracy = (minAccSeekBar.progress * 10).toFloat()
                minAccTextView.text = minAccuracy.toString() + "m"
            }

            override fun onStartTrackingTouch(seekBar: SeekBar?) {
                minDistance = (minAccSeekBar.progress * 10).toFloat()
                minAccTextView.text = minAccuracy.toString() + "m"
            }

            override fun onStopTrackingTouch(seekBar: SeekBar?) {
                minDistance = (minAccSeekBar.progress * 10).toFloat()
                minAccTextView.text = minAccuracy.toString() + "m"
            }
        })

        // Listener per minSumSeekBar
        minSumSeekBar.setOnSeekBarChangeListener(object : SeekBar.OnSeekBarChangeListener {
            override fun onProgressChanged(seekBar: SeekBar?, progress: Int, fromUser: Boolean) {
                minSum = (minSumSeekBar.progress * 10).toFloat()
                minSumTextView.text = minSum.toString() + "m"
            }

            override fun onStartTrackingTouch(seekBar: SeekBar?) {
                minSum = (minSumSeekBar.progress * 10).toFloat()
                minSumTextView.text = minSum.toString() + "m"
            }

            override fun onStopTrackingTouch(seekBar: SeekBar?) {
                minSum = (minSumSeekBar.progress * 10).toFloat()
                minSumTextView.text = minSum.toString() + "m"
            }
        })

        // FINE VIEW DEBUG -----------------------------------------------

        restoreState(savedInstanceState)

        // Registro receiver
        locationBroadcastReceiver = LocationBroadcastReceiver(statusTextView, accuracyTextView, distanceTextView)
        registerReceiver(locationBroadcastReceiver, IntentFilter("location-update"))

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

        // Listener per avviare il servizio
        startServiceButton.setOnClickListener{
            // PARAMETRI DI DEBUG
            serviceIntent.putExtra("minDistance", minDistance)
            serviceIntent.putExtra("minAccuracy", minAccuracy)
            serviceIntent.putExtra("minSum", minSum)
            startForegroundService(serviceIntent)
        }

        // Listener per fermare il servizio
        stopServiceButton.setOnClickListener{
            stopService(serviceIntent)
        }

    }

    override fun onSaveInstanceState(outState: Bundle, outPersistentState: PersistableBundle) {
        outState.putCharSequence("statusTextView", statusTextView.text)
        outState.putCharSequence("accuracyTextView", accuracyTextView.text)
        outState.putCharSequence("distanceTextView", distanceTextView.text)

        outState.putInt("minDistSeekBar", minDistSeekBar.progress)
        outState.putInt("minAccSeekBar", minAccSeekBar.progress)
        outState.putInt("minSumSeekBar", minSumSeekBar.progress)

        outState.putCharSequence("minDistTextView", minDistTextView.text)
        outState.putCharSequence("minAccTextView", minAccTextView.text)
        outState.putCharSequence("minSumTextView", minSumTextView.text)

        super.onSaveInstanceState(outState, outPersistentState)
    }

    private fun restoreState(inState: Bundle?){
        if (inState == null)
            return
        statusTextView.text = inState.getCharSequence("statusTextView")
        accuracyTextView.text = inState.getCharSequence("accuracyTextView")
        distanceTextView.text = inState.getCharSequence("distanceTextView")

        minDistSeekBar.progress = inState.getInt("minDistSeekBar")
        minAccSeekBar.progress = inState.getInt("minAccSeekBar")
        minSumSeekBar.progress = inState.getInt("minSumSeekBar")

        minDistTextView.text = inState.getCharSequence("minDistTextView")
        minAccTextView.text = inState.getCharSequence("minAccTextView")
        minSumTextView.text = inState.getCharSequence("minSumTextView")
    }

    override fun onDestroy() {
        super.onDestroy()
        unregisterReceiver(locationBroadcastReceiver)
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