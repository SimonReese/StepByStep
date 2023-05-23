package it.project.appwidget.fragments

import android.content.BroadcastReceiver
import android.content.Context
import android.content.Intent
import android.content.IntentFilter
import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Button
import android.widget.TextView
import androidx.core.content.ContextCompat.registerReceiver
import androidx.core.content.ContextCompat.startForegroundService
import it.project.appwidget.LocationService
import it.project.appwidget.R
import java.text.DecimalFormat

// TODO: Rename parameter arguments, choose names that match
// the fragment initialization parameters, e.g. ARG_ITEM_NUMBER
private const val ARG_PARAM1 = "param1"
private const val ARG_PARAM2 = "param2"

/**
 * A simple [Fragment] subclass.
 * Use the [Run.newInstance] factory method to
 * create an instance of this fragment.
 */
class Run : Fragment() {

    private lateinit var distanceTextView: TextView
    private lateinit var accuracyTextView: TextView
    private lateinit var speedTextView:TextView
    private lateinit var timerTextView: TextView

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


        distanceTextView = view.findViewById<TextView>(R.id.distanceTextView)
        accuracyTextView = view.findViewById(R.id.accuracyTextView)
        speedTextView = view.findViewById<TextView>(R.id.speedTextView)
        timerTextView = view.findViewById(R.id.timerTextView)
        val startServiceButton = view.findViewById<Button>(R.id.startServiceButton)
        val stopServiceButton = view.findViewById<Button>(R.id.stopServiceButton)


        // Registro receiver
        locationBroadcastReceiver = LocationBroadcastReceiver()
        requireContext().registerReceiver(locationBroadcastReceiver, IntentFilter("location-update"))

        // Creo intent per il LocationService
        val serviceIntent = Intent(requireActivity(), LocationService::class.java)


        startServiceButton.setOnClickListener {
            requireActivity().startForegroundService(serviceIntent)
        }

        stopServiceButton.setOnClickListener {
            requireActivity().stopService(serviceIntent)
        }

        return view
    }

    companion object {
        /**
         * Use this factory method to create a new instance of
         * this fragment using the provided parameters.
         *
         * @param param1 Parameter 1.
         * @param param2 Parameter 2.
         * @return A new instance of fragment Run.
         */
        // TODO: DEVE ESSERE SINGLETON UN FRAGMENT?!?! - Verificare assolutamente
        @JvmStatic
        fun newInstance(param1: String, param2: String) =
            Run().apply {
                arguments = Bundle().apply {
                    putString(ARG_PARAM1, param1)
                    putString(ARG_PARAM2, param2)
                }
            }
    }
}