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
import java.util.concurrent.TimeUnit
import kotlin.math.roundToInt

class Home : Fragment() {

    // Views
    private lateinit var distanceTextView: TextView
    private lateinit var passiTextView: TextView

    private var distance: Float = 0f
    private var steps: Int = 0


    private lateinit var locationBroadcastReceiver: LocationBroadcastReceiver
    // Classe per ricezione broadcast messages
    private inner class LocationBroadcastReceiver(): BroadcastReceiver() {
        override fun onReceive(context: Context?, intent: Intent?) {
            Log.d("Home.LocationBroadcastReceiver", "Chiamato onReceive")
            val distloc = intent?.getFloatExtra("distance", 0f)

            distance = distloc!!
            steps = (distloc!! *3/2).roundToInt()
            distanceTextView.text = (DecimalFormat("#.#").format(distance))
            passiTextView.text = steps.toString()
        }
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        Log.d("HomeFragment", "Chiamato onCreate")

        // Registro receiver
        locationBroadcastReceiver = LocationBroadcastReceiver()
        requireActivity().registerReceiver(locationBroadcastReceiver, IntentFilter("location-update"))
    }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        Log.d("HomeFragment", "Chiamato onCreateView")
        // Inflate del layout
        return inflater.inflate(R.layout.fragment_home, container, false)
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        //TODO: chiedere permessi
        Log.d("HomeFragment", "Chiamato onViewCreated")

        distanceTextView = view.findViewById<TextView>(R.id.counterDistance)
        passiTextView = view.findViewById<TextView>(R.id.counterPassi)

        // Imposto valori textviews
        distanceTextView.text = DecimalFormat("#.#").format(distance)
        passiTextView.text = steps.toString()

        // Recupero stato del fragment, ma solo se onSaveInstanceState non è null
        if (savedInstanceState != null) {
            restoreState(savedInstanceState)
        }

        // Creo intent per il LocationService
        val serviceIntent = Intent(requireActivity(), LocationService::class.java)

    }

    override fun onSaveInstanceState(outState: Bundle) {
        Log.d("HomeFragment", "Chiamato onSaveInstanceState")
        // Salvo lo stato di tutte le Views
        outState.putCharSequence("distanceTextView_text", distanceTextView.text)
        outState.putCharSequence("passiTextView_text", passiTextView.text)
        super.onSaveInstanceState(outState)
    }

    override fun onDestroyView() {
        Log.d("HomeFragment", "Chiamato onDestroyView")
        super.onDestroyView()
    }

    override fun onDestroy() {
        Log.d("HomeFragment", "Chiamato onDestroy")
        // Tolgo registrazione receiver
        requireActivity().unregisterReceiver(locationBroadcastReceiver)
        super.onDestroy()
    }

    // Recupero stato del fragment
    private fun restoreState(inState: Bundle) {
        Log.d("HomeFragment", "Chiamato restoreState")

        // Ripristino stato delle textviews
        distanceTextView.text = inState.getCharSequence("distanceTextView_text")
        passiTextView.text = inState.getCharSequence("passiTextView_text")
    }
}