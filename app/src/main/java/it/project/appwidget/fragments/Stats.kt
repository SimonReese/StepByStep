package it.project.appwidget.fragments

import android.os.Bundle
import android.util.Log
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Button
import android.widget.TextView
import androidx.lifecycle.lifecycleScope
import androidx.recyclerview.widget.RecyclerView
import it.project.appwidget.BarChart
import it.project.appwidget.Datasource
import it.project.appwidget.R
import it.project.appwidget.util.WeekHelpers
import it.project.appwidget.database.TrackSession
import it.project.appwidget.database.TrackSessionAdapter
import kotlinx.coroutines.launch

class Stats : Fragment() {

    // Variabili di istanza
    private val weekHelper = WeekHelpers()
    val format = "yyyy-dd-MM"

    // Views del fragment
    private lateinit var barChart: BarChart
    private lateinit var generateButton: Button
    private lateinit var pastWeekButton: Button
    private lateinit var nextWeekButton: Button
    private lateinit var currentDate: TextView
    private lateinit var recyclerView: RecyclerView

    // Stato del fragment
    private var selectedWeek = weekHelper.getWeekRange(System.currentTimeMillis()) // TODO: Spostare inizializzazioni

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
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

        // Carico dati in background
        loadRecyclerView(0, System.currentTimeMillis())

        // Riferimenti a elementi del layout
        barChart = view.findViewById(R.id.barChart)
        generateButton= view.findViewById(R.id.generateButton)
        pastWeekButton= view.findViewById(R.id.pastWeekButton)
        nextWeekButton= view.findViewById(R.id.nextWeekButton)
        currentDate= view.findViewById(R.id.tv_date)

        // Mostro etichetta settimana corrente
        currentDate.text = weekHelper.getDate(selectedWeek.first, format) + " - " + weekHelper.getDate(selectedWeek.second, format)

        // Recupero stato eventualmente salvato
        if (savedInstanceState != null){
            restoreState(savedInstanceState) //TODO: dato che si fa un restore solo della settimana corrente, coverrebbe ripristinare in onCreate
        }
        //Carica dati settimana selezionata
        loadGraph(currentDate, barChart)


        //Bottone settimana corrente
        generateButton.setOnClickListener { generateButton: View ->
            selectedWeek = weekHelper.getWeekRange(System.currentTimeMillis())
            loadGraph(currentDate, barChart)

        }

        //Bottone past week
        pastWeekButton.setOnClickListener { pastWeekButton: View ->
            selectedWeek = weekHelper.getPreviousWeekRange(selectedWeek)
            loadGraph(currentDate, barChart)
        }

        //Bottone next week
        nextWeekButton.setOnClickListener { nextWeekButton: View ->
            selectedWeek = weekHelper.getNextWeekRange(selectedWeek)
            loadGraph(currentDate, barChart)
        }

    }

    override fun onSaveInstanceState(outState: Bundle) {
        Log.d("StatsFragment", "Chiamato onSaveInstanceState")
        outState.putLongArray("selectedWeek", longArrayOf(selectedWeek.first, selectedWeek.second))
        super.onSaveInstanceState(outState)
    }

    private fun restoreState(inState: Bundle){
        Log.d("StatsFragment", "Chiamato restoreState")
        val pair = inState.getLongArray("selectedWeek")
        if (pair != null) {
            selectedWeek = Pair<Long, Long>(pair[0], pair[1])
        }
    }

    private fun loadGraph(currentDate: TextView, barChart: BarChart) {
        //Carica nel recyclerview dati della settimana selezionata
        loadRecyclerView(selectedWeek.first, selectedWeek.second)
        currentDate.text = weekHelper.getDate(selectedWeek.first, format) + " - " + weekHelper.getDate(selectedWeek.second, format)
        //Ottieni lista di TrackSession della settimana selezionata
        val sessions = getSessionsList(selectedWeek.first, selectedWeek.second)
        //Ottieni array in cui in ogni cella è presente somma distance di quel giorno
        val values: ArrayList<Double> = convertTrackSessionInDistanceArray(sessions)

        // Carico etichette nel grafico
        barChart.days = weekHelper.getDateList(selectedWeek.first, selectedWeek.second)
        // Carico valori nel grafico
        barChart.valueArray = values
    }

    private fun loadRecyclerView(from: Long, to: Long){
        // Carico dati nel recyclerview in modo asincrono
        Log.d("StatsFragment", "Imposto coroutine cariacamento dati")

        // Dall' acttivity scope avvio una nuova coroutine per caricare e impostare i dati
        lifecycleScope.launch {
            val sessionList = Datasource(requireActivity().applicationContext).getSessionListIdString(from,to)
            recyclerView.adapter = TrackSessionAdapter(sessionList)
            Log.d("AsyncStatsFragment", "Dati caricati.")
        }

        Log.d("StatsFragment", "Fine impostazione routine caricamento dati.")
    }

    /**
     * Ottiene lista di sessioni comprese tra due date espresse in Unix time.
     * @param from: data di partenza
     * @param to: data limite finale
     * @return: Una lista di oggetti TrackSession
     */
    private fun getSessionsList(from: Long, to: Long) : List<TrackSession>?{
        // Carico dati nel recyclerview in modo asincrono
        Log.d("StatsFragment", "getSessionsList()")
        var sessionList: List<TrackSession>? = null

        // Dall' activity scope avvio una nuova coroutine per caricare i dati
        lifecycleScope.launch {
            //Sostituito il context this@GraphActivity con requireActivity().applicationContext
            sessionList = Datasource(requireActivity().applicationContext).getSessionList(from,to)
            Log.d("AsyncStatsFragment", "Dati salvati")
        }

        Log.d("StatsFragment", "Ritornati")
        return sessionList
    }


    /**
     * Converte lista di TrackSession in lista di distanze
     * @param weekSession: lista di sessioni in una settimana
     * @return: un ArrayList di Double contenente la somma delle distanze giorno per giorno
     */
    private fun convertTrackSessionInDistanceArray(weekSession: List<TrackSession>?): ArrayList<Double> {
        //Lista di dimensione 7
        val distanceList: ArrayList<Double> = arrayListOf(0.0, 0.0, 0.0, 0.0, 0.0, 0.0, 0.0)
        if (!weekSession.isNullOrEmpty()) {
            for (session in weekSession) {
                distanceList[weekHelper.getNumberDayOfWeek(session.startTime)] += session.distance / 1000
            }
        }
        println(distanceList.joinToString(" "))
        return distanceList
    }

}