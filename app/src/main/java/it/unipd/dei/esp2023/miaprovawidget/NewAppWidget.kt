package it.unipd.dei.esp2023.miaprovawidget

import android.app.PendingIntent
import android.appwidget.AppWidgetManager
import android.appwidget.AppWidgetProvider
import android.content.Context
import android.content.Intent
import android.os.Bundle
import android.util.Log
import android.widget.RemoteViews


/**
 * Implementation of App Widget functionality.
 */
class NewAppWidget : AppWidgetProvider() {

    companion object {
        const val ACTION_BTN_SETTINGS = "ACTION_BTN_SETTINGS"
        const val REQUEST_CODE_BTN_SETTINGS = 1
    }


    override fun onUpdate( context: Context,  appWidgetManager: AppWidgetManager,  appWidgetIds: IntArray ) {

        for (appWidgetId in appWidgetIds) {
            val views = RemoteViews(context.packageName, R.layout.small_view_layout)
            //Quando lo posizioni small come predefinito
            RemoteViews(context.packageName, R.layout.small_view_layout)
            views.setOnClickPendingIntent(R.id.btn_settings, getPendingSelfIntent(context, ACTION_BTN_SETTINGS))
            appWidgetManager.updateAppWidget(appWidgetId, views)
        }
    }

    override fun onReceive(context: Context, intent: Intent) {
        super.onReceive(context, intent)

        if (intent.action == "ACTION_BTN_SETTINGS") {
            Log.d("Prova","ciao")
            val settingsIntent = Intent(context, SettingsActivity::class.java)
            settingsIntent.flags = Intent.FLAG_ACTIVITY_NEW_TASK
            context.startActivity(settingsIntent)
        }
    }

    override fun onEnabled(context: Context) {

    }


    override fun onDisabled(context: Context) {
        // Enter relevant functionality for when the last widget is disabled
    }

    override fun onAppWidgetOptionsChanged(context: Context, appWidgetManager: AppWidgetManager, appWidgetId: Int, newOptions: Bundle) {
        super.onAppWidgetOptionsChanged(context, appWidgetManager, appWidgetId, newOptions)

        //Dimensione corrente widget
        val minWidth = newOptions.getInt(AppWidgetManager.OPTION_APPWIDGET_MIN_WIDTH)
        val minHeight = newOptions.getInt(AppWidgetManager.OPTION_APPWIDGET_MIN_HEIGHT)

        //Imposta il layout corrispondente
        val views = when {
            minWidth <= 225 && minHeight < 130 -> {
                RemoteViews(context.packageName, R.layout.small_view_layout)
            }
            minWidth > 250 && minHeight > 130 || minWidth > 190 && minHeight > 190 -> {
                RemoteViews(context.packageName, R.layout.large_view_layout)
            }
            else -> {
                RemoteViews(context.packageName, R.layout.medium_view_layout)

            }
        }

        //Listener
        views.setOnClickPendingIntent(R.id.btn_settings, getPendingSelfIntent(context, ACTION_BTN_SETTINGS))

        appWidgetManager.updateAppWidget(appWidgetId, views)
    }


    private fun getPendingSelfIntent(context: Context, action: String): PendingIntent {
        val intent = Intent(context, NewAppWidget::class.java)
        intent.action = action
        return PendingIntent.getBroadcast(context, REQUEST_CODE_BTN_SETTINGS, intent, PendingIntent.FLAG_IMMUTABLE)
    }

}



