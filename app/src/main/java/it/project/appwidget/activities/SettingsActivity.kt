package it.project.appwidget.activities

import android.Manifest
import android.app.Activity
import android.content.Intent
import android.content.pm.PackageManager
import android.os.Build
import android.os.Bundle
import android.util.Log
import android.widget.Button
import android.widget.CheckBox
import androidx.appcompat.app.AppCompatActivity
import androidx.core.app.ActivityCompat
import androidx.core.content.ContextCompat
import androidx.core.content.PermissionChecker
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
    private var hasPermissions: Boolean = false


    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.settings_layout)

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
            setResult(RESULT_OK)
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
        hasPermissions = true // Devo supporla vera, perchè non è detto che onRequestPermissionsResult() sia stato chiamato
        if (PermissionChecker.checkSelfPermission(
                this,
                Manifest.permission.POST_NOTIFICATIONS
            ) == PermissionChecker.PERMISSION_DENIED && Build.VERSION.SDK_INT >= 33){
            //TODO: analizzare permesso POST_NOTIFICATION (pare che sia introdotto da android 13, cosa fare nel 12)?
            hasPermissions = false
            Log.w("ServiceMonitorActivity", "Permesso {POST_NOTIFICATIONS} non concesso")
        }
        if (PermissionChecker.checkSelfPermission(
                this,
                Manifest.permission.ACCESS_COARSE_LOCATION
            ) == PermissionChecker.PERMISSION_DENIED){
            hasPermissions = false
            Log.w("ServiceMonitorActivity", "Permesso {ACCESS_COARSE_LOCATION} non concesso")
        }
        if (PermissionChecker.checkSelfPermission(
                this,
                Manifest.permission.ACCESS_FINE_LOCATION
            ) == PermissionChecker.PERMISSION_DENIED){
            hasPermissions = false
            Log.w("ServiceMonitorActivity", "Permesso {ACCESS_FINE_LOCATION} non concesso")
        }

        if (!hasPermissions){
            // Chiedo tutti i permessi un una volta sola
            requestPermissions(arrayOf(
                Manifest.permission.POST_NOTIFICATIONS,
                Manifest.permission.ACCESS_COARSE_LOCATION,
                Manifest.permission.ACCESS_FINE_LOCATION), 1)
            Log.d("ServiceMonitorActivity", "Non sono stati concessi tutti i permessi necessari")
        }

    }


}
