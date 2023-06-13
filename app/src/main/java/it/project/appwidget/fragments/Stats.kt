package it.project.appwidget.fragments

import android.content.Intent
import android.os.Bundle
import android.util.Log
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Button
import android.widget.ImageButton
import android.widget.TextView
import androidx.lifecycle.lifecycleScope
import androidx.recyclerview.widget.RecyclerView
import it.project.appwidget.BarChart
import it.project.appwidget.Datasource
import it.project.appwidget.R
import it.project.appwidget.activities.DetailActivity
import it.project.appwidget.util.WeekHelper
import it.project.appwidget.database.TrackSession
import it.project.appwidget.database.TrackSessionAdapter
import kotlinx.coroutines.launch

class Stats : Fragment() {

    // Variabili di istanza
    val format = "yyyy-dd-MM"

    // Views del fragment
    private lateinit var barChart: BarChart
    private lateinit var generateButton: Button
    private lateinit var pastWeekButton: ImageButton
    private lateinit var nextWeekButton: ImageButton
    private lateinit var currentDate: TextView
    private lateinit var recyclerView: RecyclerView

    // Stato del fragment
    private lateinit var selectedWeek: Pair<Long, Long>

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        // Ripristino o creo lo stato di selectedWeek
        if (savedInstanceState != null){
            restoreState(savedInstanceState)
        } else {
            // Inizializzo settimana corrente
            selectedWeek = WeekHelper.getWeekRange(System.currentTimeMillis())
        }
        Log.d("StatsFragment", "Chiamato onCreate")
    }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        Log.d("StatsFragment", "Chiamato onCreateView")
        // Inflate del layout
        return inflater.inflate(R.layout.fragment_stats, container, false)
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        Log.d("StatsFragment", "Chiamato onViewCreated")

        // Imposto RecyclerView
        recyclerView = view.findViewById(R.id.recyclerView)
        // Riferimenti a elementi del layout
        barChart = view.findViewById(R.id.barChart)
        generateButton= view.findViewById(R.id.generateButton)
        pastWeekButton= view.findViewById(R.id.pastWeekButton)
        nextWeekButton= view.findViewById(R.id.nextWeekButton)
        currentDate= view.findViewById(R.id.tv_date)


        // Mostro etichetta settimana corrente
        currentDate.text = String.format("%s - %s", WeekHelper.getDate(selectedWeek.first, format), WeekHelper.getDate(selectedWeek.second, format))

        // Carico dati in background
        loadRecyclerView()

        //Carica dati settimana selezionata
        loadBarChart()

        //Bottone settimana corrente
        generateButton.setOnClickListener { generateButton: View ->
            selectedWeek = WeekHelper.getWeekRange(System.currentTimeMillis())
            // Mostro etichetta settimana corrente
            currentDate.text = String.format("%s - %s", WeekHelper.getDate(selectedWeek.first, format), WeekHelper.getDate(selectedWeek.second, format))
            loadBarChart()
            loadRecyclerView()
        }

        //Bottone past week
        pastWeekButton.setOnClickListener { pastWeekButton: View ->
            selectedWeek = WeekHelper.getPreviousWeekRange(selectedWeek)
            // Mostro etichetta settimana precedente
            currentDate.text = String.format("%s - %s", WeekHelper.getDate(selectedWeek.first, format), WeekHelper.getDate(selectedWeek.second, format))
            loadBarChart()
            loadRecyclerView()
        }

        //Bottone next week
        nextWeekButton.setOnClickListener { nextWeekButton: View ->
            selectedWeek = WeekHelper.getNextWeekRange(selectedWeek)
            // Mostro etichetta settimana successiva
            //currentDate.text = "${weekHelper.getDate(selectedWeek.first, format)} - ${weekHelper.getDate(selectedWeek.second, format)}"
            currentDate.text = String.format("%s - %s", WeekHelper.getDate(selectedWeek.first, format), WeekHelper.getDate(selectedWeek.second, format))
            loadBarChart()
            loadRecyclerView()
        }

    }

    override fun onSaveInstanceState(outState: Bundle) {
        Log.d("StatsFragment", "Chiamato onSaveInstanceState")
        outState.putLongArray("selectedWeek", longArrayOf(selectedWeek.first, selectedWeek.second))
        super.onSaveInstanceState(outState)
    }

    /**
     * Ripristina lo stato di [selectedWeek] recuperandone i valori dal Bundle [inState]
     */
    private fun restoreState(inState: Bundle){
        Log.d("StatsFragment", "Chiamato restoreState")
        val pair = inState.getLongArray("selectedWeek")
        if (pair != null) {
            selectedWeek = Pair<Long, Long>(pair[0], pair[1])
        }
    }

    /**
     * Carica il [BarChart] con i dati della settimana di riferimento in modo asincrono.
     * Appena i dati e le etichette sono pronti, vengono inseriti nel [BarChart], che viene rigenerato.
     */
    private fun loadBarChart() {
        // Lancio coroutine per caricare dati e etichette nel grafico
        lifecycleScope.launch {
            val trackSessionList = Datasource(requireActivity().applicationContext).getSessionList(selectedWeek.first, selectedWeek.second)
            val values = WeekHelper.convertTrackSessionInDistanceArray(trackSessionList)
            barChart.days = WeekHelper.getDateList(selectedWeek.first, selectedWeek.second)
            barChart.valueArray = values
        }
    }

    /**
     * Carica il recyclerView con i dati della settimana di riferimento in modo asincrono.
     * Appena i dati sono pronti, vengono inseriti nel recyclerView tramite l'adapter [TrackSessionAdapter].
     */
    private fun loadRecyclerView(){
        // Carico dati nel recyclerview in modo asincrono
        Log.d("StatsFragment", "Imposto coroutine cariacamento dati")

        // Creo listener da passare all'adapter
        val onClickListener: (TrackSession) -> Unit = {trackSession ->
            // Creo intent per lanciare activity
            val intent = Intent(requireContext(), DetailActivity::class.java)
            intent.putExtra(DetailActivity.ARG_SESSION_ID, trackSession.id)
            requireContext().startActivity(intent)
        }

        // Dall' activity scope avvio una nuova coroutine per caricare e impostare i dati nell'adapter
        lifecycleScope.launch {
            val trackSessionList: ArrayList<TrackSession> = Datasource(requireActivity().applicationContext).getSessionList(selectedWeek.first, selectedWeek.second)
            recyclerView.adapter = TrackSessionAdapter(trackSessionList, onClickListener)
            Log.d("AsyncStatsFragment", "Dati caricati.")
        }
        Log.d("StatsFragment", "Fine impostazione routine caricamento dati.")
    }

}