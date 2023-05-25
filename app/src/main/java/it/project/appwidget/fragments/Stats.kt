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
import it.project.appwidget.getDate
import kotlinx.coroutines.launch

class Stats : Fragment() {

    private lateinit var generateButton: Button
    private lateinit var pastWeekButton: Button
    private lateinit var nextWeekButton: Button
    private lateinit var currentDate: TextView
    private lateinit var recyclerView: RecyclerView
    private val weekHelper = WeekHelpers()
    val format = "yyyy-dd-MM"
    private var selectedWeek = weekHelper.getWeekRange(System.currentTimeMillis())

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
    }

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View? {

        val view =  inflater.inflate(R.layout.fragment_stats, container, false)

        // Imposto RecyclerView
        recyclerView = view.findViewById(R.id.recyclerView)

        // Carico dati in background
        loadRecyclerView(0, System.currentTimeMillis())

        generateButton= view.findViewById(R.id.generateButton)
        pastWeekButton= view.findViewById(R.id.pastWeekButton)
        nextWeekButton= view.findViewById(R.id.nextWeekButton)
        currentDate= view.findViewById(R.id.tv_date)

        currentDate.text = getDate(selectedWeek.first, format) + " - " + getDate(selectedWeek.second, format)

        val barChart: BarChart = view.findViewById(R.id.barChart)

        //Carica dati settimana corrente
        loadGraph(currentDate, barChart)


        //Bottone settimana corrente
        generateButton.setOnClickListener{generateButton: View ->
            generateButton as Button

            selectedWeek = weekHelper.getWeekRange(System.currentTimeMillis())
            loadGraph(currentDate, barChart)

        }

        //Bottone past week
        pastWeekButton.setOnClickListener{pastWeekButton: View ->
            pastWeekButton as Button

            selectedWeek = weekHelper.getPreviousWeekRange(selectedWeek)
            loadGraph(currentDate, barChart)
        }

        //Bottone next week
        nextWeekButton.setOnClickListener{nextWeekButton: View ->
            nextWeekButton as Button

            selectedWeek = weekHelper.getNextWeekRange(selectedWeek)
            loadGraph(currentDate, barChart)
        }

        // Inflate the layout for this fragment
        return view
    }

    private fun loadGraph(currentDate: TextView, barChart: BarChart)
    {
        //Carica del recyclerview dati settimana selezionata
        loadRecyclerView(selectedWeek.first, selectedWeek.second)
        currentDate.text = getDate(selectedWeek.first, format) + " - " + getDate(selectedWeek.second, format)
        //Ottieni lista di TrackSession della settimana selezionata
        val sessions = getSessionsList(selectedWeek.first, selectedWeek.second)
        //Ottieni array in cui in ogni cella è presente somma distance di quel giorno
        val values: Array<Int> = convertTrackSessionInDistanceArray(sessions)
        barChart.valueArray = values.toIntArray()
    }

    private fun loadRecyclerView(from: Long, to: Long){
        // Carico dati nel recyclerview in modo asincrono
        Log.d("GraphActivity", "Imposto coroutine cariacamento dati")

        // Dall' acttivity scope avvio una nuova coroutine per caricare e impostare i dati
        lifecycleScope.launch {
            val sessionList = Datasource(requireActivity().applicationContext).getSessionListIdString(from,to)
            recyclerView.adapter = TrackSessionAdapter(sessionList)
            Log.d("AsyncGraphActivty", "Dati caricati.")
        }

        Log.d("GraphActivity", "Fine impostazione routine caricamento dati.")
    }

    private fun getSessionsList(from: Long, to: Long) : List<TrackSession>?{
        // Carico dati nel recyclerview in modo asincrono
        Log.d("GraphActivity", "getSessionsList()")
        var sessionList: List<TrackSession>? = null

        // Dall' activity scope avvio una nuova coroutine per caricare i dati
        lifecycleScope.launch {
            //Sostituito il context this@GraphActivity con requireActivity().applicationContext
            sessionList = Datasource(requireActivity().applicationContext).getSessionList(from,to)
            Log.d("AsyncGraphActivty", "Dati salvati")
        }

        Log.d("GraphActivity", "Ritornati")
        return sessionList
    }


    //Converte List di TrackSession in Array di distance
    private fun convertTrackSessionInDistanceArray(weekSession: List<TrackSession>?): Array<Int>
    {
        //Lista di dimensione 7
        val graphList: MutableList<Int> = MutableList(7) { 0 }
        if (!weekSession.isNullOrEmpty())
        {
            for (session in weekSession) {
                graphList[weekHelper.getNumberDayOfWeek(session.startTime)] += session.distance.toInt()
            }

        }
        println(graphList.joinToString(" "))
        return graphList.toTypedArray()

    }


}