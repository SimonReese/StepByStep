package it.project.appwidget.widgets

import android.app.PendingIntent
import android.appwidget.AppWidgetManager
import android.appwidget.AppWidgetProvider
import android.content.Context
import android.content.Intent
import android.os.Bundle
import android.util.Log
import android.widget.RemoteViews
import it.project.appwidget.R
import it.project.appwidget.ListWidgetService
import it.project.appwidget.activities.DetailActivity

//TODO: refactor nome in ListWidgetProvider
class ListWidget : AppWidgetProvider() {

    override fun onUpdate(context: Context, appWidgetManager: AppWidgetManager, appWidgetIds: IntArray) {

        // Per ogni widget associato a questo provider
        for (appWidgetId in appWidgetIds) {
            val remoteViews = RemoteViews(context.packageName, R.layout.list_widget)

            // Creazione dell'intent per avvio servizio di gestione caricamento dei dati nella ListView
            val remoteAdaperIntent = Intent(context, ListWidgetService::class.java)
            remoteAdaperIntent.putExtra(AppWidgetManager.EXTRA_APPWIDGET_ID, appWidgetId) // Invio id widget al service

            // Collega l'intent del servizio al layout della ListView del widget
            remoteViews.setRemoteAdapter(R.id.widget_listview, remoteAdaperIntent)

            // Creazione di un intent per la gestione dei click sugli elementi della ListView
            val clickIntent = Intent(context, DetailActivity::class.java)
            clickIntent.flags = Intent.FLAG_ACTIVITY_NEW_TASK

            // Creazione di un PendingIntent che apre una Activity
            //MUTABILI E IMMUTABILI -> inviati più volte o inviato una volta (per la sicurezza)
            val clickPendingIntent = PendingIntent.getActivity(context, 0, clickIntent, PendingIntent.FLAG_MUTABLE)

            // Imposta il PendingIntent come template per gli elementi della ListView del widget
            // https://developer.android.com/reference/android/widget/RemoteViews#setPendingIntentTemplate(int,%20android.app.PendingIntent)
            //necessario per avere un template predefinito per ogni tipo di ListView
            remoteViews.setPendingIntentTemplate(R.id.widget_listview, clickPendingIntent)

            // Richiedo aggiornamento del widget con le nuove views
            appWidgetManager.updateAppWidget(appWidgetId, remoteViews)
        }

        super.onUpdate(context, appWidgetManager, appWidgetIds)
    }

    override fun onReceive(context: Context, intent: Intent) {
        super.onReceive(context, intent)
        Log.d("ListWidget", "Chiamato onReceive con intent:  $intent" )
    }

    override fun onAppWidgetOptionsChanged(context: Context?, appWidgetManager: AppWidgetManager?, appWidgetId: Int, newOptions: Bundle?) {
        super.onAppWidgetOptionsChanged(context, appWidgetManager, appWidgetId, newOptions)

        // Quando viene aggiornata la dimensione, mostro/nascondo i TextViews a seconda
    }

}

