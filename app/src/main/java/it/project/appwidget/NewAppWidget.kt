package it.project.appwidget

import android.app.PendingIntent
import android.appwidget.AppWidgetManager
import android.appwidget.AppWidgetProvider
import android.content.Context
import android.content.Intent
import android.os.Bundle
import android.provider.Settings
import android.util.Log
import android.widget.RemoteViews

class NewAppWidget : AppWidgetProvider() {

    companion object {
        const val ACTION_BTN_SETTINGS = "ACTION_BTN_SETTINGS"
        const val REQUEST_CODE_BTN_SETTINGS = 1
    }

    override fun onUpdate(context: Context, appWidgetManager: AppWidgetManager, appWidgetIds: IntArray) {

        //Ciclo tutti i widget
        for (appWidgetId in appWidgetIds) {
            //Ottengo le views
            val views = RemoteViews(context.packageName, R.layout.small_view_layout)
            //Imposto intent al click
            views.setOnClickPendingIntent(R.id.btn_settings, getPendingSelfIntent(context, ACTION_BTN_SETTINGS))
            //Aggiorno le RemoteViews associate al widget
            appWidgetManager.updateAppWidget(appWidgetId, views)
        }
    }

    override fun onReceive(context: Context, intent: Intent) {
        super.onReceive(context, intent)

        //Controllo se il bottone è stato premuto
        if(intent.action == ACTION_BTN_SETTINGS ){
            Log.d("Prova", "ciao")
            // Creo intent per lanciare SettingsActivity
            val settingsIntent = Intent(context, SettingsActivity::class.java)
            // Imposto flag per creare l'activity in una nuova task (questa flag potrebbe non funzionare correttamente, vedi https://stackoverflow.com/questions/9772927/flag-activity-new-task-clarification-needed)
            settingsIntent.flags = Intent.FLAG_ACTIVITY_NEW_TASK
            // Lacio intent
            context.startActivity(settingsIntent)
        }
    }

    override fun onAppWidgetOptionsChanged(context: Context, appWidgetManager: AppWidgetManager, appWidgetId: Int, newOptions: Bundle) {
        super.onAppWidgetOptionsChanged(context, appWidgetManager, appWidgetId, newOptions)

        //Leggo dimensione impostata
        val minWidth = newOptions.getInt(AppWidgetManager.OPTION_APPWIDGET_MIN_WIDTH)
        val minHeight = newOptions.getInt(AppWidgetManager.OPTION_APPWIDGET_MIN_HEIGHT)

        val views = when {
            minWidth <= 255 && minHeight < 130 -> {RemoteViews(context.packageName, R.layout.small_view_layout)}

            (minWidth > 250 && minHeight > 130) || (minWidth > 190 && minHeight > 190) -> {RemoteViews(context.packageName, R.layout.large_view_layout)}

            else -> {RemoteViews(context.packageName, R.layout.medium_view_layout)}
        }

        // Imposto Listener sul bottone
        views.setOnClickPendingIntent(R.id.btn_settings, getPendingSelfIntent(context, ACTION_BTN_SETTINGS))

        //Aggiorno widget
        appWidgetManager.updateAppWidget(appWidgetId, views)
    }

    private fun getPendingSelfIntent(context: Context, action: String): PendingIntent{
        val intent = Intent(context, NewAppWidget::class.java)
        intent.action = action
        return PendingIntent.getBroadcast(context, REQUEST_CODE_BTN_SETTINGS, intent, PendingIntent.FLAG_IMMUTABLE)
    }

}