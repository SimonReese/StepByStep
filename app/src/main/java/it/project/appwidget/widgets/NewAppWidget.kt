package it.project.appwidget.widgets

import android.app.PendingIntent
import android.appwidget.AppWidgetManager
import android.appwidget.AppWidgetProvider
import android.content.ComponentName
import android.content.Context
import android.content.Intent
import android.os.Bundle
import android.util.Log
import android.view.View
import android.widget.RemoteViews
import it.project.appwidget.LocationService
import it.project.appwidget.R
import it.project.appwidget.WidgetSettingsSharedPrefsHelper
import it.project.appwidget.util.WeekHelper
import java.text.DecimalFormat


class NewAppWidget : AppWidgetProvider() {

    companion object {
        const val ACTION_BTN_SAVE = "ACTION_BTN_SAVE" // Azione per il pulsante di salvataggio
    }

    private val SHARED_PREFS_NAME = "NewAppWidget"
    private lateinit var widgetSettingsSharedPrefsHelper: WidgetSettingsSharedPrefsHelper // Oggetto helper per le preferenze condivise
    private val format = "HH:mm"
    private val singleDecimal = DecimalFormat("#.#")
    private val doubleDecimal = DecimalFormat("#.##")
    private var serviceIsRunningFlag: Boolean = false


    // Override del metodo onUpdate per aggiornare il widget
    override fun onUpdate(context: Context, appWidgetManager: AppWidgetManager, appWidgetIds: IntArray) {
        Log.d("onUpdate", "Widget posizionato")


        // Ciclo tutti i widget
        for (appWidgetId in appWidgetIds) {
            // Ottengo la view in base alla dimensione
            val views = getWidgetSize(context, appWidgetId)

            // Carica il testo precedentemente salvato e impostalo sul TextView
            val savedText = loadText(context, appWidgetId, "position")
            var locationArray = savedText.split(",").toTypedArray()
            if (locationArray.size != 2)
            {
                locationArray = arrayOf("","")
            }
            views.setTextViewText(R.id.tv_value_latitude, locationArray[0])
            views.setTextViewText(R.id.tv_value_longitude, locationArray[1])
            val savedSpeed = loadText(context, appWidgetId, "speed")
            views.setTextViewText(R.id.tv_value_speed, savedSpeed)
            val savedDate = loadText(context, appWidgetId, "startTime")
            views.setTextViewText(R.id.tv_timeData, savedDate)
            val savedCalories = loadText(context, appWidgetId, "calories")
            views.setTextViewText(R.id.tv_value_calories, savedCalories)

            // Creo intent per il service
            val serviceIntent = Intent(context, LocationService::class.java)
            //StartServiceButton
            val startPendingIntent = PendingIntent.getForegroundService(context, appWidgetId,
                serviceIntent,
                PendingIntent.FLAG_IMMUTABLE
            )
            views.setOnClickPendingIntent(R.id.startServiceButton, startPendingIntent)

            // Creo intent per il service che passa come azione "STOP-SERVICE"
            val stopServiceIntent = Intent(context, LocationService::class.java)
            stopServiceIntent.action = "STOP-LOCATION-SERVICE"
            //StopServiceButton
            val stopPendingIntent = PendingIntent.getForegroundService(context, appWidgetId,
                stopServiceIntent,
                PendingIntent.FLAG_IMMUTABLE
            )
            views.setOnClickPendingIntent(R.id.stopServiceButton, stopPendingIntent)

            if(serviceIsRunningFlag)
            {
                views.setBoolean(R.id.startServiceButton, "setEnabled", false)
                views.setBoolean(R.id.stopServiceButton, "setEnabled", true)
            }
            else
            {
                views.setBoolean(R.id.startServiceButton, "setEnabled", true)
                views.setBoolean(R.id.stopServiceButton, "setEnabled", false)
            }




            // Imposto elementi layout in base a quanto indicato nelle preferences
            setNewViewVisibility(context, views)

            appWidgetManager.updateAppWidget(appWidgetId, views)
        }

    }

    // Override del metodo onReceive per gestire gli intent ricevuti
    override fun onReceive(context: Context, intent: Intent) {
        super.onReceive(context, intent)

        Log.d("NewAppWidget", "Intent " + intent.action + " ricevuto")

        // Controllo se è stato mandato l'intent dal bottone save nell'activity impostazioni
        if (intent.action == ACTION_BTN_SAVE) {
            Log.d("onReceive", ACTION_BTN_SAVE)
            val appWidgetManager = AppWidgetManager.getInstance(context)
            val appWidgetIds = appWidgetManager.getAppWidgetIds(ComponentName(context, NewAppWidget::class.java))
            for (appWidgetId in appWidgetIds) {
                val views = getWidgetSize(context, appWidgetId)
                // Chiamo il metodo setNewViewVisibility
                setNewViewVisibility(context, views)
                appWidgetManager.updateAppWidget(appWidgetId, views)
                onUpdate(context, appWidgetManager, appWidgetIds)
            }
        }

        if (intent.action == "stop-service")
        {
            val appWidgetManager = AppWidgetManager.getInstance(context)
            val appWidgetIds = appWidgetManager.getAppWidgetIds(ComponentName(context, NewAppWidget::class.java))
            for (appWidgetId in appWidgetIds) {
                val views = getWidgetSize(context, appWidgetId)
                views.setBoolean(R.id.startServiceButton, "setEnabled", true)
                views.setBoolean(R.id.stopServiceButton, "setEnabled", false)
                appWidgetManager.updateAppWidget(appWidgetId, views)
                saveServiceRunningFlag(context, false) // Salva il valore "false" nelle preferenze
            }
        }

        if (intent.action == "location-update")
        {
            val appWidgetManager = AppWidgetManager.getInstance(context)
            val appWidgetIds = appWidgetManager.getAppWidgetIds(ComponentName(context, NewAppWidget::class.java))
            for (appWidgetId in appWidgetIds) {
                val views = getWidgetSize(context, appWidgetId)
                saveServiceRunningFlag(context, true) // Salva il valore "true" nelle preferenze
                views.setBoolean(R.id.startServiceButton, "setEnabled", false)
                views.setBoolean(R.id.stopServiceButton, "setEnabled", true)
                appWidgetManager.updateAppWidget(appWidgetId, views)
                updateLocationText(context,
                    intent.getDoubleExtra("latitude", 0.0),
                    intent.getDoubleExtra("longitude", 0.0),
                    intent.getFloatExtra("distance", 0F),
                    intent.getFloatExtra("speed", 0F),
                    intent.getLongExtra("startTime", 0),
                    intent. getFloatExtra("calories", 0F))
            }
        }

    }

    // Override del metodo onDeleted per gestire l'eliminazione del widget
    override fun onDeleted(context: Context, appWidgetIds: IntArray?) {
        super.onDeleted(context, appWidgetIds)
        Log.d("onDeleted", "Widget eliminato")
    }

    // Override del metodo onAppWidgetOptionsChanged per gestire il ridimensionamento del widget
    override fun onAppWidgetOptionsChanged(context: Context, appWidgetManager: AppWidgetManager, appWidgetId: Int, newOptions: Bundle) {
        super.onAppWidgetOptionsChanged(context, appWidgetManager, appWidgetId, newOptions)
        Log.d("onAppWidgetOptionsChanged", "Widget ridimensionato")
        // Ottengo nuova view in base alle nuove dimensioni
        val views = getWidgetSize(context, appWidgetId)

        val sharedPreferences = context.getSharedPreferences(SHARED_PREFS_NAME, Context.MODE_PRIVATE)
        serviceIsRunningFlag = sharedPreferences.getBoolean("serviceIsRunningFlag", false)

        // Carica il testo salvato esistente precedentemente al resize e impostalo sul TextView
        val savedPosition = loadText(context, appWidgetId, "position")
        var locationArray = savedPosition.split(",").toTypedArray()
        if (locationArray.size != 2)
        {
            locationArray = arrayOf("","")
        }
        views.setTextViewText(R.id.tv_value_latitude, locationArray[0])
        views.setTextViewText(R.id.tv_value_longitude, locationArray[1])
        val savedSumDistance = loadText(context, appWidgetId, "sumDistance")
        views.setTextViewText(R.id.tv_value_sumDistance, savedSumDistance)
        val savedSpeed = loadText(context, appWidgetId, "speed")
        views.setTextViewText(R.id.tv_value_speed, savedSpeed)
        val savedDate = loadText(context, appWidgetId, "startTime")
        views.setTextViewText(R.id.tv_sessionDate, savedDate)
        val savedCalories = loadText(context, appWidgetId, "calories")
        views.setTextViewText(R.id.tv_value_calories, savedCalories)

        println("layoutId: " + context.resources.getResourceEntryName(views.layoutId))

        // Creo intent per il service
        val serviceIntent = Intent(context, LocationService::class.java)
        //StartServiceButton
        val startPendingIntent = PendingIntent.getForegroundService(context, appWidgetId,
            serviceIntent,
            PendingIntent.FLAG_IMMUTABLE
        )
        views.setOnClickPendingIntent(R.id.startServiceButton, startPendingIntent)

        // Creo intent per il service che passa come azione "STOP-SERVICE"
        val stopServiceIntent = Intent(context, LocationService::class.java)
        stopServiceIntent.action = "STOP-LOCATION-SERVICE"
        //StopServiceButton
        val stopPendingIntent = PendingIntent.getForegroundService(context, appWidgetId,
            stopServiceIntent,
            PendingIntent.FLAG_IMMUTABLE
        )
        views.setOnClickPendingIntent(R.id.stopServiceButton, stopPendingIntent)

        if(serviceIsRunningFlag)
        {
            views.setBoolean(R.id.startServiceButton, "setEnabled", false)
            views.setBoolean(R.id.stopServiceButton, "setEnabled", true)
        }
        else
        {
            views.setBoolean(R.id.startServiceButton, "setEnabled", true)
            views.setBoolean(R.id.stopServiceButton, "setEnabled", false)
        }

        // Aggiorno impostazioni
        setNewViewVisibility(context, views)
        // Aggiorno widget
        appWidgetManager.updateAppWidget(appWidgetId, views)
    }

    // Metodo chiamato da LocationService ogni volta che riceve un nuovo dato
    private fun updateLocationText(
        context: Context,
        latitude: Double,
        longitude: Double,
        sumDistance: Float,
        speed: Float,
        startTime: Long,
        calories: Float
    ) {
        val appWidgetManager = AppWidgetManager.getInstance(context)
        // Ottiene id widget
        val thisAppWidgetComponentName = ComponentName(context.packageName, javaClass.name)
        val appWidgetIds = appWidgetManager.getAppWidgetIds(thisAppWidgetComponentName)

        for (appWidgetId in appWidgetIds) {
            val views = getWidgetSize(context, appWidgetId)
            val updatedDistance = "$latitude,$longitude"
            var locationArray = updatedDistance.split(",").toTypedArray()
            if (locationArray.size != 2)
            {
                locationArray = arrayOf("","")
            }
            val updatedSumDistance = doubleDecimal.format(sumDistance/1000).toString() + "km"
            val updatedSpeed = doubleDecimal.format(speed).toString()
            val sessionDate = WeekHelper.getDate(startTime, format)
            val updateCalories = singleDecimal.format(calories).toString()


            // Imposta testo widget
            views.setTextViewText(R.id.tv_value_latitude, locationArray[0])
            views.setTextViewText(R.id.tv_value_longitude, locationArray[1])
            views.setTextViewText(R.id.tv_value_sumDistance, updatedSumDistance)
            views.setTextViewText(R.id.tv_value_speed, updatedSpeed)
            views.setTextViewText(R.id.tv_sessionDate, sessionDate)
            views.setTextViewText(R.id.tv_value_calories, updateCalories)




            // Salva il testo aggiornato
            saveText(context, appWidgetId, "position", updatedDistance)
            saveText(context, appWidgetId, "sumDistance", updatedSumDistance)
            saveText(context, appWidgetId, "speed" ,updatedSpeed)
            saveText(context, appWidgetId, "startTime" ,sessionDate)
            saveText(context, appWidgetId, "calories" ,updateCalories)

            appWidgetManager.updateAppWidget(appWidgetId, views)
        }
    }

    // Determina cosa mostrare sull'UI del widget in base a quanto salvato in sharedPrefsHelper
    private fun setNewViewVisibility(context: Context, views: RemoteViews) {
        widgetSettingsSharedPrefsHelper = WidgetSettingsSharedPrefsHelper(context)
        // Ottiene stati sharedPrefsHelper
        val isSpeedChecked = widgetSettingsSharedPrefsHelper.isSpeedChecked()
        val isPositionChecked = widgetSettingsSharedPrefsHelper.isDistanceChecked()
        val isCaloriesChecked = widgetSettingsSharedPrefsHelper.isCaloriesChecked()
        val isSessionDistanceChecked = widgetSettingsSharedPrefsHelper.isSessionDistanceChecked()

        // Aggiorna la visibilità dei campi nel layout del widget in base allo stato dei checkbox
        if (!isSpeedChecked && !isCaloriesChecked && !isSessionDistanceChecked)
        {
            views.setViewVisibility(R.id.left_layout, View.GONE)
        }
        else
        {
            views.setViewVisibility(R.id.left_layout, View.VISIBLE)
            views.setViewVisibility(R.id.tv_speed, if (isSpeedChecked) View.VISIBLE else View.GONE)
            views.setViewVisibility(R.id.tv_value_speed, if (isSpeedChecked) View.VISIBLE else View.GONE)
            views.setViewVisibility(R.id.tv_calories, if (isCaloriesChecked) View.VISIBLE else View.GONE)
            views.setViewVisibility(R.id.tv_value_calories, if (isCaloriesChecked) View.VISIBLE else View.GONE)
            views.setViewVisibility(R.id.tv_sumDistance, if (isSessionDistanceChecked) View.VISIBLE else View.GONE)
            views.setViewVisibility(R.id.tv_value_sumDistance, if (isSessionDistanceChecked) View.VISIBLE else View.GONE)
        }

        views.setViewVisibility(R.id.tv_latitude, if (isPositionChecked) View.VISIBLE else View.GONE)
        views.setViewVisibility(R.id.tv_value_latitude, if (isPositionChecked) View.VISIBLE else View.GONE)
        views.setViewVisibility(R.id.tv_longitude, if (isPositionChecked) View.VISIBLE else View.GONE)
        views.setViewVisibility(R.id.tv_value_longitude, if (isPositionChecked) View.VISIBLE else View.GONE)

        views.setViewVisibility(R.id.tv_sessionDate, View.VISIBLE)
    }

    // Restituisce una RemoteViews in base alle dimensioni del widget
    //ritorna layout in base alle dimensioni del widget
    private fun getWidgetSize(context: Context, widgetId: Int) :RemoteViews
    {
        val appWidgetManager = AppWidgetManager.getInstance(context)
        //Ottieni oggetto Bundle che contiene informazioni aggiuntive sul widget di ID widgetId
        //Bundle contiene le informazioni sulle dimensioni del widgett
        val options: Bundle = appWidgetManager.getAppWidgetOptions(widgetId)

        //Ottiene dimensione attuale widget
        val minWidth = options.getInt(AppWidgetManager.OPTION_APPWIDGET_MIN_WIDTH)
        val minHeight = options.getInt(AppWidgetManager.OPTION_APPWIDGET_MIN_HEIGHT)
        //println(minWidth)
        //println(minHeight)


        //Determina view in base a dimensione
        val views = when {
            minWidth <= 255 && minHeight < 188 -> {RemoteViews(context.packageName,
                R.layout.small_view_layout
            )}

            minWidth <= 255 && minHeight > 188 -> {RemoteViews(context.packageName,
                R.layout.long_view_layout
            )}

            (minWidth > 255 && minHeight > 121) || (minWidth > 190 && minHeight > 190) -> {RemoteViews(context.packageName,
                R.layout.large_view_layout
            )}

            else -> {RemoteViews(context.packageName, R.layout.medium_view_layout)}
        }

        return views
    }


    // Salva il testo del TextView distanza nel file delle preferenze condivise
    private fun saveText(context: Context, appWidgetId: Int, fieldName: String, text: String) {
        val prefs = context.getSharedPreferences(SHARED_PREFS_NAME, Context.MODE_PRIVATE)
        val editor = prefs.edit()
        editor.putString("$appWidgetId-$fieldName", text)
        editor.apply()
    }

    // Carica il testo del TextView distanza dal file delle preferenze condivise

    private fun loadText(context: Context, appWidgetId: Int, fieldName: String): String {
        val prefs = context.getSharedPreferences(SHARED_PREFS_NAME, Context.MODE_PRIVATE)
        if (fieldName == "startTime")
        {
            return prefs.getString("$appWidgetId-$fieldName", "00:00") ?: "Data"

        }
        return prefs.getString("$appWidgetId-$fieldName", "") ?: ""
    }

    // Salva lo stato del service, utilizzato poi per salvare lo stato dei bottoni al resize del widget
    private fun saveServiceRunningFlag(context: Context, flag: Boolean) {
        val sharedPreferences = context.getSharedPreferences(SHARED_PREFS_NAME, Context.MODE_PRIVATE)
        val editor = sharedPreferences.edit()
        editor.putBoolean("serviceIsRunningFlag", flag)
        editor.apply()
    }


}
