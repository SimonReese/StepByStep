package it.project.appwidget.fragments

import android.Manifest
import android.content.BroadcastReceiver
import android.content.ComponentName
import android.content.Context
import android.content.Intent
import android.content.IntentFilter
import android.os.Build
import android.os.Bundle
import android.os.SystemClock
import android.util.Log
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Button
import android.widget.Chronometer
import android.widget.TextView
import androidx.core.content.PermissionChecker
import androidx.core.content.PermissionChecker.checkSelfPermission
import it.project.appwidget.LocationService
import it.project.appwidget.R
import it.project.appwidget.widgets.NewAppWidget
import java.text.DecimalFormat
import java.util.concurrent.TimeUnit

class Run : Fragment() {

    // Variabile per gestione dei permessi
    private var hasPermissions: Boolean = false

    // Views
    private lateinit var distanceTextView: TextView
    private lateinit var rateTextView: TextView
    private lateinit var kcalTextView: TextView
    private lateinit var sessionChronometer: Chronometer
    private lateinit var startServiceButton: Button
    private lateinit var stopServiceButton: Button

    // Debug
    private lateinit var accuracy_debug_textview: TextView
    private lateinit var speed_debug_textview: TextView
    private lateinit var distance_debug_textview: TextView

    // Stato
    private var runningChronometer = false

    private lateinit var locationBroadcastReceiver: LocationBroadcastReceiver
    // TODO: Come rendo il timer consistente anche a seguito della chiusura del fragemnts?

    // Classe per ricezione broadcast messages
    private inner class LocationBroadcastReceiver : BroadcastReceiver() {
        override fun onReceive(context: Context?, intent: Intent) {
            Log.d("Run.LocationBroadcastReceiver", "Chiamato onReceive con intent " + intent.action)

            if (intent.action == "location-update")
            {
                val speedloc = intent.getFloatExtra("speed", 0f)
                val accloc = intent.getFloatExtra("accuracy", 0f)
                val distloc = intent.getFloatExtra("distance", 0f)
                val rate = intent.getFloatExtra("rate", 0f)
                val calories = intent.getFloatExtra("calories", 0f)

                val noDecimalFormat = DecimalFormat("#")
                val singleDecimal = DecimalFormat("#.#")
                val doubleDecimal = DecimalFormat("#.##")

                if (!runningChronometer) {
                    var elapsedloc =
                        intent.getLongExtra("startTime_elapsedRealtimeNanos",0) // Ottengo lo start time della prima location rispetto al boot di sistema
                    // Converto nanosecondi in millisecondi e imposto base cronometro
                    elapsedloc = TimeUnit.NANOSECONDS.toMillis(elapsedloc)
                    sessionChronometer.base = elapsedloc
                    sessionChronometer.start()
                    runningChronometer = true

                    // Inoltre devo anche scambiare lo stato dei bottoni
                    startServiceButton.isEnabled = false
                    stopServiceButton.isEnabled = true
                }
                rateTextView.text = singleDecimal.format(rate)
                distanceTextView.text = doubleDecimal.format(distloc / 1000)
                kcalTextView.text = noDecimalFormat.format(calories)

                // Debug
                speed_debug_textview.text = "speed: " + (doubleDecimal.format(speedloc!! * 3.6)) + "km/h"
                accuracy_debug_textview.text = "accuracy: " + accloc.toString() + "m"
                distance_debug_textview.text = "distance: " + distloc.toString() + "m"
            }

            if (intent.action == "stop-service")
            {
                sessionChronometer.stop()
                runningChronometer = false

                // Disattiva il bottone stopServiceButton e attiva il bottone startServiceButton
                stopServiceButton.isEnabled = false
                startServiceButton.isEnabled = true
            }

        }
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        // Creo BroadcastReceiver
        locationBroadcastReceiver = LocationBroadcastReceiver()
        Log.d("RunFragment", "Chiamato onCreate")
    }

    // La documentazione di Android dice di usare questo metodo solo per caricare il layout
    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        Log.d("RunFragment", "Chiamato onCreateView")
        // Inflate del layout
        return inflater.inflate(R.layout.fragment_run, container, false)
    }

    // E' consigliato implementare la logica del fragment qua
    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        //TODO: chiedere permessi
        Log.d("RunFragment", "Chiamato onViewCreated")

        // Inizializzazione Views
        distanceTextView = view.findViewById(R.id.distanceTextView)
        rateTextView = view.findViewById(R.id.rateTextView)
        kcalTextView = view.findViewById(R.id.kcalTextView)
        sessionChronometer = view.findViewById(R.id.sessionChronometer)
        startServiceButton = view.findViewById(R.id.startServiceButton)
        stopServiceButton = view.findViewById(R.id.stopServiceButton)
        // Views di DEBUG
        accuracy_debug_textview = view.findViewById(R.id.debug_accuracy_textview)
        speed_debug_textview = view.findViewById(R.id.debug_speed_textview)
        distance_debug_textview = view.findViewById(R.id.debug_distance_textview)
        stopServiceButton.isEnabled = false

        // Registro receiver
        requireActivity().registerReceiver(locationBroadcastReceiver, IntentFilter("location-update"))
        requireActivity().registerReceiver(locationBroadcastReceiver, IntentFilter("stop-service"))

        // Recupero stato del fragment, ma solo se onSaveInstanceState non è null
        if (savedInstanceState != null) {
            restoreState(savedInstanceState)
        }

        // Creo intent per il LocationService
        val serviceIntent = Intent(requireActivity(), LocationService::class.java)


        startServiceButton.setOnClickListener {
            // Controllo permessi
            hasPermissions = true // Devo supporla vera, perchè non è detto che onRequestPermissionsResult() sia stato chiamato
            if (checkSelfPermission(requireActivity(),Manifest.permission.POST_NOTIFICATIONS) == PermissionChecker.PERMISSION_DENIED && Build.VERSION.SDK_INT >= 33){
                //TODO: analizzare permesso POST_NOTIFICATION (pare che sia introdotto da android 13, cosa fare nel 12)?
                hasPermissions = false
                Log.w("ServiceMonitorActivity", "Permesso {POST_NOTIFICATIONS} non concesso")
            }
            if (checkSelfPermission(requireActivity(),Manifest.permission.ACCESS_COARSE_LOCATION) == PermissionChecker.PERMISSION_DENIED){
                hasPermissions = false
                Log.w("ServiceMonitorActivity", "Permesso {ACCESS_COARSE_LOCATION} non concesso")
            }
            if (checkSelfPermission(requireActivity(),Manifest.permission.ACCESS_FINE_LOCATION) == PermissionChecker.PERMISSION_DENIED){
                hasPermissions = false
                Log.w("ServiceMonitorActivity", "Permesso {ACCESS_FINE_LOCATION} non concesso")
            }

            if (!hasPermissions){
                // Chiedo tutti i permessi un una volta sola
                requestPermissions(arrayOf(
                    Manifest.permission.POST_NOTIFICATIONS,
                    Manifest.permission.ACCESS_COARSE_LOCATION,
                    Manifest.permission.ACCESS_FINE_LOCATION), 1)
                Log.d("ServiceMonitorActivity", "Non sono stati concessi tutti i permessi necessari")
            }
            else
            {
                requireActivity().startForegroundService(serviceIntent)
                sessionChronometer.base = SystemClock.elapsedRealtime()
                sessionChronometer.start()
                runningChronometer = true

                // Disattiva il bottone startServiceButton e attiva il bottone stopServiceButton
                startServiceButton.isEnabled = false
                stopServiceButton.isEnabled = true
            }
        }

        stopServiceButton.setOnClickListener {
            // Ferma il servizio
            requireActivity().stopService(serviceIntent)
            sessionChronometer.stop()
            runningChronometer = false

            // Disattiva il bottone stopServiceButton e attiva il bottone startServiceButton
            stopServiceButton.isEnabled = false
            startServiceButton.isEnabled = true
        }
    }

    override fun onDestroyView() {
        Log.d("RunFragment", "Chiamato onDestroyView")
        // Tolgo registrazione receiver
        requireActivity().unregisterReceiver(locationBroadcastReceiver)
        super.onDestroyView()
    }

    // Viene chiamato solo quando activity chiama lo stesso!! Non va bene per il salvataggio in navigazione
    override fun onSaveInstanceState(outState: Bundle) {
        Log.d("RunFragment", "Chiamato onSaveInstanceState")

        // Salvo cronometro, ma solo se attivo
        if (runningChronometer){
            outState.putBoolean("runningChronometer", runningChronometer)
            outState.putLong("sessionChronometer_base", sessionChronometer.base)
        }

        // Salvo lo stato di tutte le Views
        outState.putCharSequence("distanceTextView_text", distanceTextView.text)
        outState.putCharSequence("rateTextView_text", rateTextView.text)
        outState.putCharSequence("kcalTextView_text", kcalTextView.text)
        outState.putBoolean("stopServiceButton", stopServiceButton.isEnabled)
        outState.putBoolean("startServiceButton", startServiceButton.isEnabled)

        super.onSaveInstanceState(outState)
    }

    override fun onDestroy() {
        Log.d("RunFragment", "Chiamato onDestroy")
        super.onDestroy()
    }

    // Recupero stato del fragment
    private fun restoreState(inState: Bundle) {
        Log.d("RunFragment", "Chiamato restoreState")

        // Ottengo stato cronometro e lo faccio ripartire, ma solo se era attivo
        runningChronometer = inState.getBoolean("runningChronometer", false)
        if (runningChronometer){
            sessionChronometer.base = inState.getLong("sessionChronometer_base")
            sessionChronometer.start()
        }

        // Ripristino stato delle textviews
        distanceTextView.text = inState.getCharSequence("distanceTextView_text")
        rateTextView.text = inState.getCharSequence("rateTextView_text")
        kcalTextView.text = inState.getCharSequence("kcalTextView_text")
        stopServiceButton.isEnabled =  inState.getBoolean("stopServiceButton" )
        startServiceButton.isEnabled =  inState.getBoolean("startServiceButton" )

    }
}
