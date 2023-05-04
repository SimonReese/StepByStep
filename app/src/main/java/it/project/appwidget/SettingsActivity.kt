package it.project.appwidget

import android.os.Bundle
import android.widget.ArrayAdapter
import android.widget.Spinner
import androidx.appcompat.app.AppCompatActivity

class SettingsActivity:AppCompatActivity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.impostazioni)

        //Documentazione: https://developer.android.com/develop/ui/views/components/spinner
        val spinner: Spinner = findViewById(R.id.units)

        //Creo ArrayAdapter di stringhe dall'array di stringe definito sul file strings.xml
        ArrayAdapter.createFromResource(this, R.array.unità, android.R.layout.simple_spinner_item).also {
            adapter ->

            adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item)
            spinner.adapter = adapter
        }


    }
}