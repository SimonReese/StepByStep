package it.project.appwidget.activities

import android.app.Activity
import android.content.Intent
import android.content.pm.PackageManager
import android.os.Bundle
import android.util.Log
import android.widget.Button
import android.widget.CheckBox
import androidx.appcompat.app.AppCompatActivity
import androidx.core.app.ActivityCompat
import androidx.core.content.ContextCompat
import it.project.appwidget.widgets.NewAppWidget
import it.project.appwidget.R
import it.project.appwidget.WidgetSettingsSharedPrefsHelper


class SettingsActivity : AppCompatActivity() {

    companion object {
        const val MY_PERMISSIONS_REQUEST_LOCATION = 123
    }
    private lateinit var widgetSettingsSharedPrefsHelper: WidgetSettingsSharedPrefsHelper
    private lateinit var cbSpeed: CheckBox
    private lateinit var cbDistance: CheckBox
    private lateinit var cbCalories: CheckBox
    private lateinit var cbSessionDistance: CheckBox

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.impostazioni)

        //Creo oggetto SharedPrefsHelper
        widgetSettingsSharedPrefsHelper = WidgetSettingsSharedPrefsHelper(this)

        cbSpeed = findViewById(R.id.cb_speed)
        cbDistance = findViewById(R.id.cb_distance)
        cbCalories = findViewById(R.id.cb_calories)
        cbSessionDistance = findViewById(R.id.cb_session_distance)

        val saveButton: Button = findViewById(R.id.btn_save)
        //setOnClickListener del saveButton
        saveButton.setOnClickListener {
            //Salvo stato CheckBox
            widgetSettingsSharedPrefsHelper.setSpeedChecked(cbSpeed.isChecked)
            widgetSettingsSharedPrefsHelper.setDistanceChecked(cbDistance.isChecked)
            widgetSettingsSharedPrefsHelper.setCaloriesChecked(cbCalories.isChecked)
            widgetSettingsSharedPrefsHelper.setSessionDistanceChecked(cbSessionDistance.isChecked)
            //Lancio intent
            val intent = Intent(this, NewAppWidget::class.java)
            intent.action = NewAppWidget.ACTION_BTN_SAVE
            sendBroadcast(intent)
            finish()
        }
        //Permette di avere inizialmente (prima volta in assoluto che apro le settings) tutti i check a true
        val isFirstLaunch = widgetSettingsSharedPrefsHelper.isFirstLaunch()
        println(isFirstLaunch)
        if (isFirstLaunch) {
            widgetSettingsSharedPrefsHelper.setSpeedChecked(true)
            widgetSettingsSharedPrefsHelper.setDistanceChecked(true)
            widgetSettingsSharedPrefsHelper.setCaloriesChecked(true)
            widgetSettingsSharedPrefsHelper.setSessionDistanceChecked(true)
            widgetSettingsSharedPrefsHelper.setFirstLaunch(false)

        }

        cbSpeed.isChecked = widgetSettingsSharedPrefsHelper.isSpeedChecked()
        cbDistance.isChecked = widgetSettingsSharedPrefsHelper.isDistanceChecked()
        cbCalories.isChecked = widgetSettingsSharedPrefsHelper.isCaloriesChecked()
        cbSessionDistance.isChecked = widgetSettingsSharedPrefsHelper.isSessionDistanceChecked()

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
