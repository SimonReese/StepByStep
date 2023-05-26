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
    private lateinit var accuracyTextView: TextView
    private lateinit var speedTextView:TextView
    private lateinit var sessionChronometer: Chronometer
    private lateinit var startServiceButton: Button
    private lateinit var stopServiceButton: Button

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
            speedTextView.text = (DecimalFormat("#.##").format(speedloc!! * 3.6)).toString() + "km/h"
            accuracyTextView.text = accloc.toString() + "m"
            distanceTextView.text = distloc.toString() + "m"
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
        accuracyTextView = view.findViewById<TextView>(R.id.rateTextView)
        speedTextView = view.findViewById<TextView>(R.id.kcalTextView)
        sessionChronometer = view.findViewById<Chronometer>(R.id.sessionChronometer)
        startServiceButton = view.findViewById<Button>(R.id.startServiceButton)
        stopServiceButton = view.findViewById<Button>(R.id.stopServiceButton)

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
        outState.putCharSequence("accuracyTextView_text", accuracyTextView.text)
        outState.putCharSequence("speedTextView_text", speedTextView.text)

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
        accuracyTextView.text = inState.getCharSequence("accuracyTextView_text")
        speedTextView.text = inState.getCharSequence("speedTextView_text")

    }
}
