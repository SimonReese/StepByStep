package it.project.appwidget

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
import androidx.core.content.ContextCompat.startForegroundService
import androidx.work.OneTimeWorkRequestBuilder
import androidx.work.PeriodicWorkRequestBuilder
import androidx.work.WorkManager
import java.util.concurrent.TimeUnit


class NewAppWidget : AppWidgetProvider() {

    companion object {
        const val ACTION_BTN_SETTINGS = "ACTION_BTN_SETTINGS"
        const val ACTION_BTN_SAVE = "ACTION_BTN_SAVE"
        const val REQUEST_CODE_BTN_SETTINGS = 1
    }

    private lateinit var sharedPrefsHelper: SharedPrefsHelper


    //Salva il testo di "tv_distance"
    private fun saveTvDistanceText(context: Context, appWidgetId: Int, text: String) {
        val sharedPreferences = context.getSharedPreferences("NewAppWidget", Context.MODE_PRIVATE)
        sharedPreferences.edit().putString("tv_distance_text_$appWidgetId", text).apply()
    }

    //carica  il testo di "tv_distance"
    private fun loadTvDistanceText(context: Context, appWidgetId: Int): String {
        val sharedPreferences = context.getSharedPreferences("NewAppWidget", Context.MODE_PRIVATE)
        return sharedPreferences.getString("tv_distance_text_$appWidgetId", "Latitudine: 0.0, Longitudine: 0.0") ?: "Latitudine: 0.0, Longitudine: 0.0"
    }



    override fun onUpdate(context: Context, appWidgetManager: AppWidgetManager, appWidgetIds: IntArray) {
        Log.d("onUpdate", "Widget posizionato")
        //Ciclo tutti i widget
        for (appWidgetId in appWidgetIds) {
            //Ottengo le views
            val views = getWidgetSize(context, appWidgetId)
            //Imposto intent al click
            views.setOnClickPendingIntent(R.id.btn_settings, getPendingSelfIntent(context, ACTION_BTN_SETTINGS))
            // Carica il testo salvato e impostalo sul TextView
            val savedText = loadTvDistanceText(context, appWidgetId)
            views.setTextViewText(R.id.tv_distance, savedText)
            //Imposto elementi layout in base a quanto indicato nelle preferences
            setNewViewVisibility(context,views)

            appWidgetManager.updateAppWidget(appWidgetId, views)
        }

    }





    override fun onEnabled(context: Context) {
        super.onEnabled(context)
    }

    override fun onReceive(context: Context, intent: Intent) {
        super.onReceive(context, intent)

        // Controllo se il bottone apri impostazioni è stato premuto
        if (intent.action == ACTION_BTN_SETTINGS) {
            Log.d("OnReceive", "Bottone impostazioni premuto")
            // Creo intent per lanciare SettingsActivity
            val settingsIntent = Intent(context, SettingsActivity::class.java)
            // Imposto flag per creare l'activity in una nuova task
            settingsIntent.flags = Intent.FLAG_ACTIVITY_NEW_TASK
            // Lancio intent
            context.startActivity(settingsIntent)
        }

        // Controllo se il bottone save nelle impostazioni è stato premuto (oppure premuto back da SettingsActivity)
        if (intent.action == ACTION_BTN_SAVE) {
            Log.d("onReceive", ACTION_BTN_SAVE)
            val appWidgetManager = AppWidgetManager.getInstance(context)
            val appWidgetIds = appWidgetManager.getAppWidgetIds(ComponentName(context, NewAppWidget::class.java))
            for (appWidgetId in appWidgetIds) {
                val views = getWidgetSize(context, appWidgetId)
                setNewViewVisibility(context, views)
                appWidgetManager.updateAppWidget(appWidgetId, views)
            }
        }
    }

    override fun onDeleted(context: Context, appWidgetIds: IntArray?) {
        super.onDeleted(context, appWidgetIds)
        Log.d("onDeleted", "Widget eliminato")
    }

    override fun onAppWidgetOptionsChanged(context: Context, appWidgetManager: AppWidgetManager, appWidgetId: Int, newOptions: Bundle) {
        super.onAppWidgetOptionsChanged(context, appWidgetManager, appWidgetId, newOptions)
        Log.d("onAppWidgetOptionsChanged", "Widget ridimensionato")
        val views = getWidgetSize(context, appWidgetId)

        // Carica il testo salvato e impostalo sul TextView
        val savedDistance = loadTvDistanceText(context, appWidgetId)
        views.setTextViewText(R.id.tv_distance, savedDistance)
        //TODO: fare la stessa cosa con tv_sumDistance e tv_tempo_trascorso

        //Aggiorno impostazioni
        setNewViewVisibility(context,views)

        // Imposto Listener sul bottone
        views.setOnClickPendingIntent(R.id.btn_settings, getPendingSelfIntent(context, NewAppWidget.ACTION_BTN_SETTINGS))


        // Aggiorno widget
        appWidgetManager.updateAppWidget(appWidgetId, views)
    }


    fun updateLocationText(context: Context, latitude: Double, longitude: Double, sumDistance: Float, StartSessionTime: Long) {
        val appWidgetManager = AppWidgetManager.getInstance(context)
        val thisAppWidgetComponentName = ComponentName(context.packageName, javaClass.name)
        val appWidgetIds = appWidgetManager.getAppWidgetIds(thisAppWidgetComponentName)

        for (appWidgetId in appWidgetIds) {
            val views = getWidgetSize(context, appWidgetId)
            val updatedText = "Latitudine: $latitude, Longitudine: $longitude"
            views.setTextViewText(R.id.tv_distance, updatedText)
            views.setTextViewText(R.id.tv_sumDistance, "Session distance: $sumDistance")
            views.setTextViewText(R.id.tv_tempo_trascorso, "Time: $StartSessionTime")
            appWidgetManager.updateAppWidget(appWidgetId, views)

            // Salva il testo aggiornato
            saveTvDistanceText(context, appWidgetId, updatedText)
        }
    }

    private fun setNewViewVisibility(context: Context, views: RemoteViews)
    {

        sharedPrefsHelper = SharedPrefsHelper(context)
        val isSpeedChecked = sharedPrefsHelper.isSpeedChecked()
        val isDistanceChecked = sharedPrefsHelper.isDistanceChecked()
        val isCaloriesChecked = sharedPrefsHelper.isCaloriesChecked()
        val isSessionDistanceChecked = sharedPrefsHelper.isSessionDistanceChecked()
        val isTimeChecked = sharedPrefsHelper.isTimeChecked()

        // Aggiorna la visibilità dei campi nel layout del widget in base allo stato dei checkbox
        views.setViewVisibility(R.id.tv_speed, if (isSpeedChecked) View.VISIBLE else View.GONE)
        views.setViewVisibility(R.id.tv_distance, if (isDistanceChecked) View.VISIBLE else View.GONE)
        views.setViewVisibility(R.id.tv_calories, if (isCaloriesChecked) View.VISIBLE else View.GONE)
        views.setViewVisibility(R.id.tv_sumDistance, if (isSessionDistanceChecked) View.VISIBLE else View.GONE)
        views.setViewVisibility(R.id.tv_tempo_trascorso, if (isTimeChecked) View.VISIBLE else View.GONE)
        // Imposto Listener sul bottone
        views.setOnClickPendingIntent(R.id.btn_settings, getPendingSelfIntent(context, NewAppWidget.ACTION_BTN_SETTINGS))
    }



}

private fun getPendingSelfIntent(context: Context, action: String): PendingIntent{
    val intent = Intent(context, NewAppWidget::class.java)
    intent.action = action
    return PendingIntent.getBroadcast(context,
        NewAppWidget.REQUEST_CODE_BTN_SETTINGS, intent, PendingIntent.FLAG_IMMUTABLE)
}

fun getWidgetSize(context: Context, widgetId: Int) :RemoteViews
{
    val appWidgetManager = AppWidgetManager.getInstance(context)
    val options: Bundle = appWidgetManager.getAppWidgetOptions(widgetId)

    val minWidth = options.getInt(AppWidgetManager.OPTION_APPWIDGET_MIN_WIDTH)
    val minHeight = options.getInt(AppWidgetManager.OPTION_APPWIDGET_MIN_HEIGHT)


    val views = when {
        minWidth <= 255 && minHeight < 130 -> {RemoteViews(context.packageName, R.layout.small_view_layout)}

        (minWidth > 250 && minHeight > 130) || (minWidth > 190 && minHeight > 190) -> {RemoteViews(context.packageName, R.layout.large_view_layout)}

        else -> {RemoteViews(context.packageName, R.layout.medium_view_layout)}
    }

    return views
}