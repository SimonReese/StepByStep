package it.project.appwidget

import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.util.Log
import android.view.View
import android.widget.Button
import android.widget.TextView
import androidx.lifecycle.lifecycleScope
import androidx.recyclerview.widget.RecyclerView
import it.project.appwidget.database.TrackSession
import it.project.appwidget.database.TrackSessionAdapter
import kotlinx.coroutines.launch

class GraphActivity : AppCompatActivity() {


    private lateinit var recyclerView: RecyclerView
    private val weekHelper = WeekHelpers()
    private val format = "yyyy-dd-MM"
    private var selectedWeek = weekHelper.getWeekRange(System.currentTimeMillis())

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_graph)

        // Imposto RecyclerView
        recyclerView = findViewById(R.id.recyclerView)

        // Carico dati in background
        loadRecyclerView(0, System.currentTimeMillis())

        //TODO: aggiungere bottone che mostra a schermo soltanto le query dell'ultima settimana attraverso Datasource.getSessionListFromTo(from, to)

        val generateButton: Button = findViewById(R.id.generateButton)
        val pastWeekButton: Button = findViewById(R.id.pastWeekButton)
        val nextWeekButton: Button = findViewById(R.id.nextWeekButton)
        val currentDate: TextView = findViewById(R.id.tv_date)

        currentDate.text = getDate(selectedWeek.first, format) + " - " + getDate(selectedWeek.second, format)

        val barChart: BarChart = findViewById(R.id.barChart)

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
            val sessionList = Datasource(this@GraphActivity).getSessionListIdString(from,to)
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
             sessionList = Datasource(this@GraphActivity).getSessionList(from,to)
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