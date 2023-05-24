package it.project.appwidget.fragments

import android.content.BroadcastReceiver
import android.content.Context
import android.content.Intent
import android.content.IntentFilter
import android.os.Bundle
import android.os.SystemClock
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

    private lateinit var distanceTextView: TextView
    private lateinit var accuracyTextView: TextView
    private lateinit var speedTextView:TextView
    private lateinit var sessionChronometer: Chronometer

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

    }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        // Inflate the layout for this fragment
        val view =  inflater.inflate(R.layout.fragment_run, container, false)

        //TODO: chiedere permessi

        distanceTextView = view.findViewById<TextView>(R.id.distanceTextView)
        accuracyTextView = view.findViewById(R.id.accuracyTextView)
        speedTextView = view.findViewById<TextView>(R.id.speedTextView)
        sessionChronometer = view.findViewById(R.id.sessionChronometer)
        val startServiceButton = view.findViewById<Button>(R.id.startServiceButton)
        val stopServiceButton = view.findViewById<Button>(R.id.stopServiceButton)


        // Registro receiver
        locationBroadcastReceiver = LocationBroadcastReceiver()
        requireContext().registerReceiver(locationBroadcastReceiver, IntentFilter("location-update"))

        // Creo intent per il LocationService
        val serviceIntent = Intent(requireActivity(), LocationService::class.java)


        startServiceButton.setOnClickListener {
            requireActivity().startForegroundService(serviceIntent)
            sessionChronometer.base = SystemClock.elapsedRealtime()
            sessionChronometer.start()
        }

        stopServiceButton.setOnClickListener {
            requireActivity().stopService(serviceIntent)
            sessionChronometer.stop()
        }

        return view
    }
}
