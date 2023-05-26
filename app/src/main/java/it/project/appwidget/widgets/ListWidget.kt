package it.project.appwidget.widgets

import android.app.PendingIntent
import android.appwidget.AppWidgetManager
import android.appwidget.AppWidgetProvider
import android.content.Context
import android.content.Intent
import android.util.Log
import android.widget.RemoteViews
import androidx.lifecycle.lifecycleScope
import it.project.appwidget.Datasource
import it.project.appwidget.R
import it.project.appwidget.ListWidgetService
import it.project.appwidget.activities.DetailActivity
import it.project.appwidget.database.TrackSession
import kotlinx.coroutines.launch

class ListWidget : AppWidgetProvider() {

    override fun onUpdate(context: Context, appWidgetManager: AppWidgetManager, appWidgetIds: IntArray) {
        for (appWidgetId in appWidgetIds) {
            val remoteViews = RemoteViews(context.packageName, R.layout.list_widget)

            // Creazione dell'intent per il servizio che gestisce il caricamento dei dati nella ListView
            val intent = Intent(context, ListWidgetService::class.java)
            intent.putExtra(AppWidgetManager.EXTRA_APPWIDGET_ID, appWidgetId)

            // Collega l'intent al layout della ListView del widget
            remoteViews.setRemoteAdapter(R.id.widget_listview, intent)

            // Creazione dell'intent per la gestione dei clic sugli elementi della ListView
            val clickIntent = Intent(context, ListWidget::class.java)
            clickIntent.action = "ITEM_CLICK_ACTION"
            clickIntent.putExtra(AppWidgetManager.EXTRA_APPWIDGET_ID, appWidgetId)

            // Creazione del PendingIntent per l'intent di clic
            val clickPendingIntent = PendingIntent.getBroadcast(context, 0, clickIntent, PendingIntent.FLAG_IMMUTABLE)

            // Imposta il PendingIntent come template per gli elementi della ListView del widget
            remoteViews.setPendingIntentTemplate(R.id.widget_listview, clickPendingIntent)

            // Aggiornamento del widget con le nuove viste
            appWidgetManager.updateAppWidget(appWidgetId, remoteViews)
        }

        super.onUpdate(context, appWidgetManager, appWidgetIds)
    }

    override fun onReceive(context: Context, intent: Intent) {
        super.onReceive(context, intent)

        Log.d("onReceive", "Intent " + intent.action + " ricevuto")

        // Gestione del clic sugli elementi della ListView
        if (intent.action == "ITEM_CLICK_ACTION") {
            val appWidgetId = intent.getIntExtra(AppWidgetManager.EXTRA_APPWIDGET_ID, AppWidgetManager.INVALID_APPWIDGET_ID)

            if (appWidgetId != AppWidgetManager.INVALID_APPWIDGET_ID) {
                val detailIntent = Intent(context, DetailActivity::class.java)
                detailIntent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK or Intent.FLAG_ACTIVITY_MULTIPLE_TASK)
                context.startActivity(detailIntent)
            }
        }
    }


}

