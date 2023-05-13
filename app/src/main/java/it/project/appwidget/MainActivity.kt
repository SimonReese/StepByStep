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

        val graphButton: Button = findViewById(R.id.graphButton)

        graphButton.setOnClickListener{clickedButton: View ->
            clickedButton as Button //Cast a Button
            //Creo intent per aprire activity grafico
            val intent = Intent(clickedButton.context, GraphActivity::class.java)
            startActivity(intent) //Lancio nuova activity
        }
    }
}