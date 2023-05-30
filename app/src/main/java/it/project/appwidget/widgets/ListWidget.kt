package it.project.appwidget.widgets

import android.app.PendingIntent
import android.appwidget.AppWidgetManager
import android.appwidget.AppWidgetProvider
import android.content.Context
import android.content.Intent
import android.util.Log
import android.widget.RemoteViews
import it.project.appwidget.R
import it.project.appwidget.ListWidgetService
import it.project.appwidget.activities.DetailActivity

//TODO: refactor nome in ListWidgetProvider
class ListWidget : AppWidgetProvider() {

    companion object {
        // The activity argument representing session name
        const val ARG_SESSION_ID = "session:id"
    }

    override fun onUpdate(context: Context, appWidgetManager: AppWidgetManager, appWidgetIds: IntArray) {

        // Per ogni widget associato a questo provider
        for (appWidgetId in appWidgetIds) {
            val remoteViews = RemoteViews(context.packageName, R.layout.list_widget)

            // Creazione dell'intent per il servizio che gestisce il caricamento dei dati nella ListView
            val remoteAdaperIntent = Intent(context, ListWidgetService::class.java)
            remoteAdaperIntent.putExtra(AppWidgetManager.EXTRA_APPWIDGET_ID, appWidgetId)

            // Collega l'intent al layout della ListView del widget
            remoteViews.setRemoteAdapter(R.id.widget_listview, remoteAdaperIntent)


            // Creazione di un intent per la gestione dei clic sugli elementi della ListView
            val clickIntent = Intent(context, DetailActivity::class.java)
            clickIntent.flags = Intent.FLAG_ACTIVITY_NEW_TASK
            //clickIntent.action = "ITEM_CLICK_ACTION"
            //clickIntent.putExtra(AppWidgetManager.EXTRA_APPWIDGET_ID, appWidgetId)

            // Creazione di un Broadcast PendingIntent
            //val clickPendingIntent = PendingIntent.getBroadcast(context, 0, clickIntent, PendingIntent.FLAG_MUTABLE)
            val clickPendingIntent = PendingIntent.getActivity(context, 0, clickIntent, PendingIntent.FLAG_MUTABLE)

            // Imposta il PendingIntent come template per gli elementi della ListView del widget
            // https://developer.android.com/reference/android/widget/RemoteViews#setPendingIntentTemplate(int,%20android.app.PendingIntent)
            remoteViews.setPendingIntentTemplate(R.id.widget_listview, clickPendingIntent)

            // Richiedo aggiornamento del widget con le nuove views
            appWidgetManager.updateAppWidget(appWidgetId, remoteViews)
        }

        super.onUpdate(context, appWidgetManager, appWidgetIds)
    }

    override fun onReceive(context: Context, intent: Intent) {
        super.onReceive(context, intent)

        Log.d("onReceive", "Intent " + intent.action + " ricevuto")

        // Controllo action intent. Se corrisponde a quella generata dagli elementi avvio gestione del clic sugli elementi della ListView
//        if (intent.action == "ITEM_CLICK_ACTION") {
//
//            // Ottengo id widget dall'intent
//            val appWidgetId = intent.getIntExtra(AppWidgetManager.EXTRA_APPWIDGET_ID, AppWidgetManager.INVALID_APPWIDGET_ID)
//            if (appWidgetId != AppWidgetManager.INVALID_APPWIDGET_ID) {
//
//                val sessionId = intent.getIntExtra("ARG_SESSION_ID",-1)
//
//                val detailIntent = Intent(context, DetailActivity::class.java)
//                detailIntent.putExtra(ARG_SESSION_ID,sessionId)
//                detailIntent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK or Intent.FLAG_ACTIVITY_MULTIPLE_TASK)
//                context.startActivity(detailIntent)
//            }
//        }
    }


}

