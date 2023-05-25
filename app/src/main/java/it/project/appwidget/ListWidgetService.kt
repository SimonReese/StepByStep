package it.project.appwidget

import android.appwidget.AppWidgetManager
import android.content.Context
import android.content.Intent
import android.widget.RemoteViews
import android.widget.RemoteViewsService
import it.project.appwidget.widgets.ListWidget

// La classe ListWidgetService è responsabile di fornire una fabbrica di visualizzatori per il widget


class ListWidgetService : RemoteViewsService() {

    // Metodo che viene chiamato quando viene richiesta la fabbrica di visualizzatori per il widget.
    override fun onGetViewFactory(intent: Intent): RemoteViewsFactory {
        return ListWidgetFactory(applicationContext, intent)
    }

    // La classe interna ListWidgetFactory implementa l'interfaccia RemoteViewsFactory e gestisce il caricamento dei dati per la ListView del widget.
    class ListWidgetFactory(private val context: Context, intent: Intent) : RemoteViewsService.RemoteViewsFactory {

        // ID del widget associato alla fabbrica
        private val appWidgetId: Int = intent.getIntExtra(AppWidgetManager.EXTRA_APPWIDGET_ID, AppWidgetManager.INVALID_APPWIDGET_ID)

        // Elenco degli elementi da visualizzare nella ListView del widget
        private val items: List<String> = listOf("Elemento 1", "Elemento 2", "Elemento 3",
            "4", "4", "4", "4", "4", "4", "4", "4")

        override fun onCreate() {
        }

        override fun onDataSetChanged() {
        }

        override fun onDestroy() {
        }

        // Restituisce il numero di elementi nella ListView del widget
        override fun getCount(): Int {
            return items.size
        }

        // Ottieni la vista per un determinato elemento della ListView del widget
        override fun getViewAt(position: Int): RemoteViews {
            val remoteViews = RemoteViews(context.packageName, R.layout.list_item_widget)

            // Imposta il testo dell'elemento corrente nella TextView all'interno dell'elemento della ListView
            remoteViews.setTextViewText(R.id.item_textview, items[position])

            val clickIntent = Intent(context, ListWidget::class.java)
            clickIntent.action = "ITEM_CLICK_ACTION"
            clickIntent.putExtra(AppWidgetManager.EXTRA_APPWIDGET_ID, appWidgetId)
            //TODO: put extra id database
            clickIntent.putExtra("position", position)
            remoteViews.setOnClickFillInIntent(R.id.list_item_widget, clickIntent)

            return remoteViews
        }

        // Restituisce una vista di caricamento personalizzata (opzionale)
        override fun getLoadingView(): RemoteViews? {
            return null
        }

        // Restituisce il numero di tipi di visualizzatori diversi (opzionale)
        override fun getViewTypeCount(): Int {
            return 1
        }

        // Restituisce l'ID dell'elemento nella posizione specificata (opzionale)
        override fun getItemId(position: Int): Long {
            return position.toLong()
        }

        // Restituisce true se gli ID degli elementi sono stabili (opzionale)
        override fun hasStableIds(): Boolean {
            return true
        }
    }
}

