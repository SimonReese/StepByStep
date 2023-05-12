package it.project.appwidget

import android.app.Activity
import android.appwidget.AppWidgetManager
import android.content.Context
import android.content.Intent
import android.content.pm.PackageManager
import android.os.Bundle
import android.util.Log
import android.view.View
import android.widget.ArrayAdapter
import android.widget.RemoteViews
import android.widget.Spinner
import androidx.appcompat.app.AppCompatActivity
import androidx.core.app.ActivityCompat
import androidx.core.content.ContextCompat
import androidx.work.PeriodicWorkRequestBuilder
import androidx.work.WorkManager
import java.util.concurrent.TimeUnit

class SettingsActivity:AppCompatActivity() {
    private var appWidgetId = AppWidgetManager.INVALID_APPWIDGET_ID

    companion object {
        const val MY_PERMISSIONS_REQUEST_LOCATION = 123

    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.impostazioni)

        //Documentazione: https://developer.android.com/develop/ui/views/components/spinner
        val spinner: Spinner = findViewById(R.id.units)

        //Creo ArrayAdapter di stringhe dall'array di stringe definito sul file strings.xml
        ArrayAdapter.createFromResource(this, R.array.units, android.R.layout.simple_spinner_item).also {
            adapter ->

            adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item)
            spinner.adapter = adapter
        }

        //RICHIEDO PERMESSI GPS
        if (ContextCompat.checkSelfPermission(this, android.Manifest.permission.ACCESS_FINE_LOCATION)
            == PackageManager.PERMISSION_GRANTED && ContextCompat.checkSelfPermission(this, android.Manifest.permission.ACCESS_COARSE_LOCATION)
            == PackageManager.PERMISSION_GRANTED
        ) {
            Log.d("SettingsActivity", "Permessi già concessi")
            // Registrazione per ricevere aggiornamenti sulla posizione dell'utente
        } else {
            Log.d("SettingsActivity", "Permessi non trovati, richiedo permessi all'utente")
            // Richiesta dei permessi all'utente
            ActivityCompat.requestPermissions(this as Activity,
                arrayOf(android.Manifest.permission.ACCESS_FINE_LOCATION, android.Manifest.permission.ACCESS_COARSE_LOCATION),
                MY_PERMISSIONS_REQUEST_LOCATION)
        }

    }
}