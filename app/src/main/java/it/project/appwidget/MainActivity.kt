package it.project.appwidget

import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.view.View
import android.widget.Button

class MainActivity : AppCompatActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)

        // Bottoni per aprire le activity graph e service monitor
        val graphButton: Button = findViewById(R.id.graphButton)
        val serviceMonitorButton: Button = findViewById(R.id.serviceMonitorButton)

        // Imposto listener per lanciare intent e aprire activity graph
        graphButton.setOnClickListener{ clickedButton: View ->
            // Creo intent e lancio activity
            val intent = Intent(this, GraphActivity::class.java)
            startActivity(intent)
        }

        // Imposto listener per lanciare intent e aprire service monitor activity
        serviceMonitorButton.setOnClickListener{ clickedButton: View ->
            // Creo intent e lancio activity
            val intent = Intent(this, ServiceMonitorActivity::class.java)
            startActivity(intent)
        }
    }
}