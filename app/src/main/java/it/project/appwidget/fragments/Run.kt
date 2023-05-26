package it.project.appwidget.fragments

import android.content.BroadcastReceiver
import android.content.Context
import android.content.Intent
import android.content.IntentFilter
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
import it.project.appwidget.LocationService
import it.project.appwidget.R
import java.text.DecimalFormat

class Run : Fragment() {

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
    private inner class LocationBroadcastReceiver(): BroadcastReceiver() {
        override fun onReceive(context: Context?, intent: Intent?) {
            val speedloc = intent?.getFloatExtra("speed", 0f)
            val accloc = intent?.getFloatExtra("accuracy", 0f)
            val distloc = intent?.getFloatExtra("distanza", 0f)
            speed_debug_textview.text = (DecimalFormat("#.##").format(speedloc!! * 3.6)).toString() + "km/h"
            accuracy_debug_textview.text = accloc.toString() + "m"
            distance_debug_textview.text = distloc.toString() + "m"
        }
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
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
        distanceTextView = view.findViewById<TextView>(R.id.distanceTextView)
        rateTextView = view.findViewById<TextView>(R.id.rateTextView)
        kcalTextView = view.findViewById<TextView>(R.id.kcalTextView)
        sessionChronometer = view.findViewById<Chronometer>(R.id.sessionChronometer)
        startServiceButton = view.findViewById<Button>(R.id.startServiceButton)
        stopServiceButton = view.findViewById<Button>(R.id.stopServiceButton)
        // Views di DEBUG
        accuracy_debug_textview = view.findViewById(R.id.debug_accuracy_textview)
        speed_debug_textview = view.findViewById(R.id.debug_speed_textview)
        distance_debug_textview = view.findViewById(R.id.debug_distance_textview)

        // Recupero stato del fragment, ma solo se onSaveInstanceState non è null
        if (savedInstanceState != null) {
            restoreState(savedInstanceState)
        }

        // Registro receiver
        locationBroadcastReceiver = LocationBroadcastReceiver()
        requireContext().registerReceiver(locationBroadcastReceiver, IntentFilter("location-update"))

        // Creo intent per il LocationService
        val serviceIntent = Intent(requireActivity(), LocationService::class.java)


        startServiceButton.setOnClickListener {
            requireActivity().startForegroundService(serviceIntent)
            sessionChronometer.base = SystemClock.elapsedRealtime()
            sessionChronometer.start()
            runningChronometer = true
        }

        stopServiceButton.setOnClickListener {
            requireActivity().stopService(serviceIntent)
            sessionChronometer.stop()
            runningChronometer = false
        }
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

    }
}
