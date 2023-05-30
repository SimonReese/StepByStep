package it.project.appwidget

import android.appwidget.AppWidgetManager
import android.content.ComponentName
import android.content.Context
import android.content.Intent
import android.util.Log
import android.widget.RemoteViews
import android.widget.RemoteViewsService
import it.project.appwidget.activities.DetailActivity.Companion.ARG_SESSION_ID
import it.project.appwidget.database.TrackSession
import it.project.appwidget.util.WeekHelpers
import it.project.appwidget.widgets.ListWidget
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Deferred
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.async
import kotlinx.coroutines.launch

// La classe ListWidgetService è responsabile di fornire una fabbrica di visualizzatori per il widget


class ListWidgetService : RemoteViewsService() {

    // crea un oggetto CoroutineScope utilizzato per avviare e gestire le coroutine in modo asincrono all'interno della classe
    private val scope = CoroutineScope(Dispatchers.Default)

    // Metodo che viene chiamato quando viene richiesta la fabbrica di visualizzatori per il widget.
    override fun onGetViewFactory(intent: Intent): RemoteViewsFactory {
        return ListWidgetFactory(applicationContext, intent)
    }

    override fun onCreate() {
        super.onCreate()
        // Inizializza il CoroutineScope
        scope
    }


    // La classe interna ListWidgetFactory implementa l'interfaccia RemoteViewsFactory e gestisce il caricamento dei dati per la ListView del widget.
    class ListWidgetFactory(private val context: Context, intent: Intent) : RemoteViewsFactory {

        private val weekHelper = WeekHelpers()
        private val format = "yyyy-dd-MM hh:mm"


        // ID del widget associato alla fabbrica
        private val appWidgetId: Int = intent.getIntExtra(AppWidgetManager.EXTRA_APPWIDGET_ID, AppWidgetManager.INVALID_APPWIDGET_ID)

        // Elenco degli elementi da visualizzare nella ListView del widget
        private lateinit var items: ArrayList<TrackSession>

        private val scope = CoroutineScope(Dispatchers.Default) // CoroutineScope all'interno della classe ListWidgetFactory

        override fun onCreate() {
            // Inizializza l'elenco items come vuoto
            items = arrayListOf()
            scope.async {
                val range = weekHelper.getWeekRange(System.currentTimeMillis())
                items = Datasource(context).getSessionList(range.first, range.second)
            }
        }

        private suspend fun getSessionsList(context: Context, from: Long, to: Long): Deferred<ArrayList<TrackSession>> {
            // Carico dati nel recyclerview in modo asincrono
            Log.d("ListWidgetService", "getSessionsList()")

            return scope.async {
                Datasource(context).getSessionList(from, to)
            }
        }

        override fun onDataSetChanged() {
            // Avvia la coroutine per ottenere i dati delle sessioni
            scope.launch {
                // Ottieni lista di TrackSession
                val sessionListDeferred = getSessionsList(context, 0, System.currentTimeMillis())
                //Quando il thread si è concluso imposta valore di items
                items = sessionListDeferred.await()
            }
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

            val itemText = weekHelper.getDate(items[position].startTime, format) + " | " + items[position].duration/1000 + "km"
            // Imposta il testo dell'elemento corrente nella TextView all'interno dell'elemento della ListView
            remoteViews.setTextViewText(R.id.item_textview, itemText)

            //Imposta intent sul singolo item della lista
            val clickIntent = Intent(context, ListWidget::class.java)
            clickIntent.action = "ITEM_CLICK_ACTION"
            clickIntent.putExtra(AppWidgetManager.EXTRA_APPWIDGET_ID, appWidgetId)
            // Inserisco Id nell'intent (putExtra FUNZIONA)
            clickIntent.putExtra("ARG_SESSION_ID", items[position].id.toString())
            //https://developer.android.com/reference/android/widget/RemoteViews#setOnClickFillInIntent(int,%20android.content.Intent)
            remoteViews.setOnClickFillInIntent(R.id.list_item_widget, clickIntent)

            return remoteViews
        }

        // Restituisce una vista di caricamento personalizzata
        override fun getLoadingView(): RemoteViews? {
            return null
        }

        // Restituisce il numero di tipi di visualizzatori diversi
        override fun getViewTypeCount(): Int {
            return 1
        }

        // Restituisce l'ID dell'elemento nella posizione specificata
        override fun getItemId(position: Int): Long {
            return position.toLong()
        }

        // Restituisce true se gli ID degli elementi sono stabili
        override fun hasStableIds(): Boolean {
            return true
        }

    }

}

