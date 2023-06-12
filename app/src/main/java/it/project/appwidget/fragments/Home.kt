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
import android.widget.ProgressBar
import android.widget.TextView
import androidx.lifecycle.lifecycleScope
import it.project.appwidget.Datasource
import it.project.appwidget.LocationService
import it.project.appwidget.R
import it.project.appwidget.UserPreferencesHelper
import it.project.appwidget.util.WeekHelpers
import kotlinx.coroutines.launch
import java.text.DecimalFormat
import java.util.Calendar
import java.util.concurrent.TimeUnit
import kotlin.math.roundToInt

/**
 * Fragment della schermata Home.
 * Nel file [R.navigation.main_navigation_graph] è specificato come fragment primario.
 *
 * Si occupa di mostrare una schermata di benvenuto all'utente, e mostra dei pannelli
 * con i progressi giornalieri di passi, distanza e calorie.
 */
class Home : Fragment() {

    // Views
    /** [TextView] relativa al nome utente (superiore) */
    private lateinit var usernameTextView: TextView
    /** [TextView] relativa ai passi giornalieri */
    private lateinit var passiTextView: TextView
    /** [TextView] relativa alla distanza giornaliera */
    private lateinit var distanceTextView: TextView
    /** [TextView] relativa alle calorie giornaliere */
    private lateinit var caloriesTextView: TextView
    /** [ProgressBar] relativa alle calorie giornaliere rispetto a quelle di obiettivo */
    private lateinit var progressBar: ProgressBar
    /** [TextView] relativa alla percentuale di calorie giornaliere bruciate */
    private lateinit var percentTextView: TextView
    /** [TextView] relativa al nome utente (inferiore) */
    private lateinit var user_name: TextView


    // Stato interno
    /** Distanza giornaliera */
    private var distance: Double = 0.0
    /** Passi giornalieri */
    private var steps: Int = 0
    /** Chilocalorie giornaliere */
    private var kcal: Int = 0
    /** Chilocalorie obiettivo */
    private var kcalTarget: Int = 0
    /** Nome utente */
    private var username: String = "Utente"
    // TODO: rendere statico
    private val weekHelper = WeekHelpers()

    /** [BroadcastReceiver] che riceve aggiornamenti alla fine delle registrazioni delle sessioni */
    private lateinit var locationBroadcastReceiver: LocationBroadcastReceiver
    // Classe per ricezione broadcast messages TODO: REIMPLEMENTARE!
    private inner class LocationBroadcastReceiver(): BroadcastReceiver() {
        override fun onReceive(context: Context?, intent: Intent?) {

            val preferencesHelper = UserPreferencesHelper(requireActivity())

            Log.d("Home.LocationBroadcastReceiver", "Chiamato onReceive")
            val distloc = intent?.getFloatExtra("distance", 0f)
            val kcalloc = intent?.getFloatExtra("calories", 0f)

            /*
            kcal += (kcalloc!!)
            distance = distloc!!
            steps = (distloc!! *3/2).roundToInt()
            distanceTextView.text = (DecimalFormat("#.#").format(distance))
            caloriesTextView.text = DecimalFormat("#.#").format(kcalloc/1000).toString() + "Kcal"
            */

            //TODO: progress = SommaCalorieOdierne + CalorieSessioneCorrente
            //progressBar.progress = progress
            println(preferencesHelper.nome)

        }
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        // Creo BroadcastReceiver
        locationBroadcastReceiver = LocationBroadcastReceiver()
        // Leggo valori stato
        if (savedInstanceState == null){
            // TODO: salvare stato
        }


        Log.d("HomeFragment", "Chiamato onCreate")
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
        Log.d("HomeFragment", "Chiamato onViewCreated")

        distance = 0.0
        steps = 0
        kcal = 0


        // Riferiementi alle Views
        distanceTextView = view.findViewById<TextView>(R.id.counterDistance)
        passiTextView = view.findViewById<TextView>(R.id.counterPassi)
        caloriesTextView = view.findViewById<TextView>(R.id.counterCalories)
        progressBar = view.findViewById<ProgressBar>(R.id.progress_bar)
        usernameTextView = view.findViewById<TextView>(R.id.nome_utente)
        percentTextView = view.findViewById<TextView>(R.id.percent)
        user_name = view.findViewById<TextView>(R.id.user_name_tv)


        // Imposto valori default textviews
        distanceTextView.text = DecimalFormat("#.#").format(distance)
        passiTextView.text = steps.toString()
        caloriesTextView.text = DecimalFormat("#.#").format(kcal/1000).toString() + "Kcal"
        progressBar.max = 100
        progressBar.progress = kcal
        usernameTextView.text = username
        user_name.text = username

        // Registro receiver
        requireActivity().registerReceiver(locationBroadcastReceiver, IntentFilter("location-update"))

        // Recupero stato del fragment, ma solo se onSaveInstanceState non è null
        if (savedInstanceState != null) {
            restoreState(savedInstanceState)
        }

        // Creo intent per il LocationService
        val serviceIntent = Intent(requireActivity(), LocationService::class.java)

        // Avvio coroutine impostazione valori
        lifecycleScope.launch {
            // Leggo da sharedpreferences
            val userPreferencesHelper = UserPreferencesHelper(requireActivity())
            username = userPreferencesHelper.nome
            kcalTarget = userPreferencesHelper.kcalTarget

            // Leggo da database
            val weekRange = weekHelper.getWeekRange(System.currentTimeMillis())
            val from = weekRange.first
            val to = weekRange.second

            val trackSessionList = Datasource(requireActivity()).getSessionList(from, to)

            for (trackSession in trackSessionList){
                distance += trackSession.distance.roundToInt()
                kcal += trackSession.kcal
                steps += (trackSession.distance * 3/2).roundToInt()
            }


            // Modifico valori views
            distanceTextView.text = distance.toString()
            passiTextView.text = steps.toString()
            caloriesTextView.text = kcal.toString()
            updateProgressBar(kcalTarget)
            usernameTextView.text = username
            user_name.text = username
        }
    }

    override fun onDestroyView() {
        Log.d("HomeFragment", "Chiamato onDestroyView")
        // Tolgo registrazione receiver
        requireActivity().unregisterReceiver(locationBroadcastReceiver)
        super.onDestroyView()
    }

    override fun onSaveInstanceState(outState: Bundle) {
        Log.d("HomeFragment", "Chiamato onSaveInstanceState")
        super.onSaveInstanceState(outState)
    }

    override fun onDestroy() {
        Log.d("HomeFragment", "Chiamato onDestroy")
        super.onDestroy()
    }

    override fun onResume() {
        super.onResume()
        // Leggo da sharedpreferences
        val userPreferencesHelper = UserPreferencesHelper(requireActivity())
        kcalTarget = userPreferencesHelper.kcalTarget
        updateProgressBar(kcalTarget)
    }

    // Recupero stato del fragment
    private fun restoreState(inState: Bundle) {
        Log.d("HomeFragment", "Chiamato restoreState")
    }

    private fun updateProgressBar(newKcalTarget: Int) {
        kcalTarget = newKcalTarget
        progressBar.max = 100
        val progress = if (kcalTarget == 0) 100.0 else kcal.toDouble() / kcalTarget.toDouble() * 100
        progressBar.progress = progress.roundToInt()
        percentTextView.text = progress.roundToInt().toString() + "%"
        println("percentuale = " + kcal + "/" + kcalTarget + "*100 = " + progressBar.progress + "%")
    }
}