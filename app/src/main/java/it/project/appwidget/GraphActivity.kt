package it.project.appwidget

import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.util.Log
import android.view.View
import android.widget.Button
import androidx.lifecycle.lifecycleScope
import androidx.recyclerview.widget.RecyclerView
import kotlinx.coroutines.delay
import kotlinx.coroutines.launch
import kotlin.random.Random

class GraphActivity : AppCompatActivity() {

    private lateinit var recyclerView: RecyclerView

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_graph)

        // Imposto RecyclerView
        recyclerView = findViewById(R.id.recyclerView)

        // Carico dati in background
        loadRecyclerView()

        //TODO: aggiungere bottone che mostra a schermo soltanto le query dell'ultima settimana attraverso Datasource.getSessionListFromTo(from, to)

        val generateButton: Button = findViewById(R.id.generateButton)
        val barChart: BarChart = findViewById(R.id.barChart)
        generateButton.setOnClickListener{generateButton: View ->
            generateButton as Button
            // Creo nuovo vettore di interi
            val values = IntArray(7) {
                Random.nextInt(100)
            }
            barChart.valueArray = values
        }
    }

    private fun loadRecyclerView(){
        // Carico dati nel recyclerview in modo asincrono
        Log.d("GraphActivity", "Imposto coroutine cariacamento dati")

        // Dall' acttivity scope avvio una nuova coroutine per caricare e impostare i dati
        lifecycleScope.launch {
            val sessionList = Datasource(this@GraphActivity).getSessionList()
            recyclerView.adapter = TrackSessionAdapter(sessionList)
            Log.d("AsyncGraphActivty", "Dati caricati.")
        }

        Log.d("GraphActivity", "Fine impostazione routine caricamento dati.")
    }
}