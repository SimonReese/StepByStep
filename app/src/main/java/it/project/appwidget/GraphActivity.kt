package it.project.appwidget

import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.view.View
import android.widget.Button
import kotlin.random.Random

class GraphActivity : AppCompatActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_graph)

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
}