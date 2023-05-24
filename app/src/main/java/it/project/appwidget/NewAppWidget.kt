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
import it.project.appwidget.activities.SettingsActivity


class NewAppWidget : AppWidgetProvider() {

    companion object {
        const val ACTION_BTN_SETTINGS = "ACTION_BTN_SETTINGS"
        const val ACTION_BTN_SAVE = "ACTION_BTN_SAVE"
        const val EXTRA_APPWIDGET_ID = 1
    }

    private val SHARED_PREFS_NAME = "NewAppWidget"
    private lateinit var sharedPrefsHelper: SharedPrefsHelper


    override fun onUpdate(context: Context, appWidgetManager: AppWidgetManager, appWidgetIds: IntArray) {
        Log.d("onUpdate", "Widget posizionato")

        //Ciclo tutti i widget
        for (appWidgetId in appWidgetIds) {
            //Ottengo la view in base alla dimensione
            val views = getWidgetSize(context, appWidgetId)
            //Imposto intent al bottone impostazioni
            views.setOnClickPendingIntent(R.id.btn_settings, getPendingSelfIntent(context, ACTION_BTN_SETTINGS))
            // Carica il testo precedentemente salvato e impostalo sul TextView
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

        Log.d("onReceive", "Intent " + intent.action + " ricevuto")

        // Controllo se il bottone impostazioni è stato premuto
        if (intent.action == ACTION_BTN_SETTINGS) {
            Log.d("OnReceive", "Bottone impostazioni premuto")
            // Creo intent per lanciare SettingsActivity
            val settingsIntent = Intent(context, SettingsActivity::class.java)
            // Imposto flag per creare l'activity in una nuova task
            settingsIntent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK or Intent.FLAG_ACTIVITY_MULTIPLE_TASK)
            // Lancio intent
            context.startActivity(settingsIntent)
        }

        // Controllo se è stato mandato l'intent dal bottone save nell'activity impostazioni
        if (intent.action == ACTION_BTN_SAVE) {
            Log.d("onReceive", ACTION_BTN_SAVE)
            val appWidgetManager = AppWidgetManager.getInstance(context)
            val appWidgetIds = appWidgetManager.getAppWidgetIds(ComponentName(context, NewAppWidget::class.java))
            for (appWidgetId in appWidgetIds) {
                val views = getWidgetSize(context, appWidgetId)
                //chiamo metodo setNewViewVisibility
                setNewViewVisibility(context, views)
                appWidgetManager.updateAppWidget(appWidgetId, views)
            }
        }

        if (intent.action.equals("location-update")) {
            Log.d("onReceive", "location-update")
            val appWidgetManager = AppWidgetManager.getInstance(context)
            val appWidgetIds = appWidgetManager.getAppWidgetIds(ComponentName(context, NewAppWidget::class.java))
            for (appWidgetId in appWidgetIds) {
                // Aggiorno il testo del widget
                updateLocationText(context,intent.getDoubleExtra("longitude", 0.0),
                    intent.getDoubleExtra("latitude", 0.0)
                    , intent.getFloatExtra("distanza", 0F))
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
        //Ottengo nuova view in base alle nuove dimensioni
        val views = getWidgetSize(context, appWidgetId)

        // Carica il testo salvato esistente precedentemente al resize e impostalo sul TextView
        val savedDistance = loadTvDistanceText(context, appWidgetId)
        views.setTextViewText(R.id.tv_distance, savedDistance)
        val savedSumDistance = loadTvSumDistanceText(context, appWidgetId)
        views.setTextViewText(R.id.tv_sumDistance, savedSumDistance)

        //Aggiorno impostazioni
        setNewViewVisibility(context,views)

        // Imposto Listener sul bottone
        views.setOnClickPendingIntent(R.id.btn_settings, getPendingSelfIntent(context, ACTION_BTN_SETTINGS))


        // Aggiorno widget
        appWidgetManager.updateAppWidget(appWidgetId, views)
    }

    // Metodo chimato de LocationService ogni volta che riceve un nuovo dato
    //
    fun updateLocationText(context: Context, latitude: Double, longitude: Double, sumDistance: Float) {
        val appWidgetManager = AppWidgetManager.getInstance(context)
        // Ottiene id widget
        val thisAppWidgetComponentName = ComponentName(context.packageName, javaClass.name)
        val appWidgetIds = appWidgetManager.getAppWidgetIds(thisAppWidgetComponentName)

        for (appWidgetId in appWidgetIds) {
            val views = getWidgetSize(context, appWidgetId)
            val updatedDistance = "Latitudine: $latitude, Longitudine: $longitude"
            val updatedSumDistance = "Session distance: $sumDistance"

            //Imposta testo widget
            views.setTextViewText(R.id.tv_distance, updatedDistance)
            views.setTextViewText(R.id.tv_sumDistance, updatedSumDistance)

            // Salva il testo aggiornato
            saveTvDistanceText(context, appWidgetId, updatedDistance)
            saveTvSumDistanceText(context, appWidgetId, updatedSumDistance)

            appWidgetManager.updateAppWidget(appWidgetId, views)

        }
    }

    // Determina cosa mostrare sull'UI del widget in base a qanto salvato in sharedPrefsHelper
    private fun setNewViewVisibility(context: Context, views: RemoteViews)
    {

        sharedPrefsHelper = SharedPrefsHelper(context)
        //Ottiene stati sharedPrefsHelper
        val isSpeedChecked = sharedPrefsHelper.isSpeedChecked()
        val isDistanceChecked = sharedPrefsHelper.isDistanceChecked()
        val isCaloriesChecked = sharedPrefsHelper.isCaloriesChecked()
        val isSessionDistanceChecked = sharedPrefsHelper.isSessionDistanceChecked()


        // Aggiorna la visibilità dei campi nel layout del widget in base allo stato dei checkbox
        views.setViewVisibility(R.id.tv_speed, if (isSpeedChecked) View.VISIBLE else View.GONE)
        views.setViewVisibility(R.id.tv_distance, if (isDistanceChecked) View.VISIBLE else View.GONE)
        views.setViewVisibility(R.id.tv_calories, if (isCaloriesChecked) View.VISIBLE else View.GONE)
        views.setViewVisibility(R.id.tv_sumDistance, if (isSessionDistanceChecked) View.VISIBLE else View.GONE)

        // Reimposto Listener sul bottone settings
        views.setOnClickPendingIntent(R.id.btn_settings, getPendingSelfIntent(context, ACTION_BTN_SETTINGS))
    }

    private fun saveTvDistanceText(context: Context, appWidgetId: Int, text: String) {
        val sharedPreferences = context.getSharedPreferences(SHARED_PREFS_NAME, Context.MODE_PRIVATE)
        sharedPreferences.edit().putString("tv_distance_text_$appWidgetId", text).apply()
    }

    private fun loadTvDistanceText(context: Context, appWidgetId: Int): String {
        val sharedPreferences = context.getSharedPreferences(SHARED_PREFS_NAME, Context.MODE_PRIVATE)
        return sharedPreferences.getString("tv_distance_text_$appWidgetId", "Latitudine: 0.0, Longitudine: 0.0") ?: "Latitudine: 0.0, Longitudine: 0.0"
    }

    private fun saveTvSumDistanceText(context: Context, appWidgetId: Int, text: String) {
        val sharedPreferences = context.getSharedPreferences(SHARED_PREFS_NAME, Context.MODE_PRIVATE)
        sharedPreferences.edit().putString("tv_sum_distance_text_$appWidgetId", text).apply()
    }

    private fun loadTvSumDistanceText(context: Context, appWidgetId: Int): String {
        val sharedPreferences = context.getSharedPreferences(SHARED_PREFS_NAME, Context.MODE_PRIVATE)
        return sharedPreferences.getString("tv_sum_distance_text_$appWidgetId", "Session distance: 0.0") ?: "Session distance: 0.0"
    }





}

private fun getPendingSelfIntent(context: Context, action: String): PendingIntent{
    val intent = Intent(context, NewAppWidget::class.java)
    intent.action = action
    return PendingIntent.getBroadcast(context,
        NewAppWidget.EXTRA_APPWIDGET_ID, intent, PendingIntent.FLAG_IMMUTABLE)
}

//ritorna layout in base alle dimensioni del widget
fun getWidgetSize(context: Context, widgetId: Int) :RemoteViews
{
    val appWidgetManager = AppWidgetManager.getInstance(context)
    //Ottieni oggetto Bundle che contiene informazioni aggiuntive sul widget di ID widgetId
    val options: Bundle = appWidgetManager.getAppWidgetOptions(widgetId)

    //Ottiene dimensione attuale widget
    val minWidth = options.getInt(AppWidgetManager.OPTION_APPWIDGET_MIN_WIDTH)
    val minHeight = options.getInt(AppWidgetManager.OPTION_APPWIDGET_MIN_HEIGHT)

    //Determina view in base a dimensione
    val views = when {
        minWidth <= 255 && minHeight < 130 -> {RemoteViews(context.packageName, R.layout.small_view_layout)}

        (minWidth > 250 && minHeight > 130) || (minWidth > 190 && minHeight > 190) -> {RemoteViews(context.packageName, R.layout.large_view_layout)}

        else -> {RemoteViews(context.packageName, R.layout.medium_view_layout)}
    }

    return views
}