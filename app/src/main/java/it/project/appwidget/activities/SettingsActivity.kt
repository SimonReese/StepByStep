package it.project.appwidget.activities

import android.app.Activity
import android.content.Intent
import android.content.pm.PackageManager
import android.os.Bundle
import android.util.Log
import android.widget.ArrayAdapter
import android.widget.Button
import android.widget.CheckBox
import android.widget.Spinner
import androidx.appcompat.app.AppCompatActivity
import androidx.core.app.ActivityCompat
import androidx.core.content.ContextCompat
import it.project.appwidget.NewAppWidget
import it.project.appwidget.R
import it.project.appwidget.SharedPrefsHelper


class SettingsActivity : AppCompatActivity() {

    companion object {
        const val MY_PERMISSIONS_REQUEST_LOCATION = 123
        const val ACTION_BTN_SAVE = "ACTION_BTN_SAVE"
    }
    private lateinit var sharedPrefsHelper: SharedPrefsHelper
    private lateinit var cbSpeed: CheckBox
    private lateinit var cbDistance: CheckBox
    private lateinit var cbCalories: CheckBox
    private lateinit var cbSessionDistance: CheckBox

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.impostazioni)

        //Creo oggetto SharedPrefsHelper
        sharedPrefsHelper = SharedPrefsHelper(this)

        cbSpeed = findViewById(R.id.cb_speed)
        cbDistance = findViewById(R.id.cb_distance)
        cbCalories = findViewById(R.id.cb_calories)
        cbSessionDistance = findViewById(R.id.cb_session_distance)

        val saveButton: Button = findViewById(R.id.btn_save)
        //setOnClickListener del saveButton
        saveButton.setOnClickListener {
            //Salvo stato CheckBox
            sharedPrefsHelper.setSpeedChecked(cbSpeed.isChecked)
            sharedPrefsHelper.setDistanceChecked(cbDistance.isChecked)
            sharedPrefsHelper.setCaloriesChecked(cbCalories.isChecked)
            sharedPrefsHelper.setSessionDistanceChecked(cbSessionDistance.isChecked)
            //Lancio intent
            val intent = Intent(this, NewAppWidget::class.java)
            intent.action = NewAppWidget.ACTION_BTN_SAVE
            sendBroadcast(intent)
            finish()
        }
        //TODO: controllare che isFirstLaunch funzioni (che al primo avvia tutti i checkbox siano true)
        //Permette di avere inizialmente (prima volta in assoluto che apro l'app) tutti i check a true
        val isFirstLaunch = sharedPrefsHelper.isFirstLaunch()
        if (isFirstLaunch) {
            sharedPrefsHelper.setSpeedChecked(true)
            sharedPrefsHelper.setDistanceChecked(true)
            sharedPrefsHelper.setCaloriesChecked(true)
            sharedPrefsHelper.setSessionDistanceChecked(true)
            sharedPrefsHelper.setFirstLaunch(false)
        } else {
            cbSpeed.isChecked = sharedPrefsHelper.isSpeedChecked()
            cbDistance.isChecked = sharedPrefsHelper.isDistanceChecked()
            cbCalories.isChecked = sharedPrefsHelper.isCaloriesChecked()
            cbSessionDistance.isChecked = sharedPrefsHelper.isSessionDistanceChecked()
        }

        //Documentazione: https://developer.android.com/develop/ui/views/components/spinner
        val spinner: Spinner = findViewById(R.id.units)
        // Creo ArrayAdapter di stringhe dall'array di stringe definito sul file strings.xml
        ArrayAdapter.createFromResource(this, R.array.units, android.R.layout.simple_spinner_item)
            .also { adapter ->
                adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item)
                spinner.adapter = adapter
            }

        //RICHIEDO PERMESSI GPS
        if (ContextCompat.checkSelfPermission(this, android.Manifest.permission.ACCESS_FINE_LOCATION)
            == PackageManager.PERMISSION_GRANTED && ContextCompat.checkSelfPermission(this, android.Manifest.permission.ACCESS_COARSE_LOCATION)
            == PackageManager.PERMISSION_GRANTED)
        {
            Log.d("SettingsActivity", "Permessi già concessi")
            // Registrazione per ricevere aggiornamenti sulla posizione dell'utente
        } else {
            Log.d("SettingsActivity", "Permessi non trovati, richiedo permessi all'utente")
            // Richiesta dei permessi all'utente
            ActivityCompat.requestPermissions(this as Activity, arrayOf
                (android.Manifest.permission.ACCESS_FINE_LOCATION, android.Manifest.permission.ACCESS_COARSE_LOCATION)
                , MY_PERMISSIONS_REQUEST_LOCATION
            )
        }

    }



    override fun onPause() {
        super.onPause()
        finish()
    }

}