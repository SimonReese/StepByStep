package it.project.appwidget

import android.appwidget.AppWidgetManager
import android.content.Context
import android.content.Intent
import android.util.Log
import android.widget.RemoteViews
import android.widget.RemoteViewsService
import it.project.appwidget.activities.DetailActivity
import it.project.appwidget.database.TrackSession
import it.project.appwidget.util.WeekHelpers
import it.project.appwidget.widgets.ListWidget
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Deferred
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.async
import kotlinx.coroutines.launch
import java.text.DecimalFormat

// La classe ListWidgetService è responsabile di fornire una fabbrica di visualizzatori per il widget

class ListWidgetService : RemoteViewsService() {

    // crea un oggetto CoroutineScope utilizzato per avviare e gestire le coroutine in modo asincrono all'interno della classe
    private val scope = CoroutineScope(Dispatchers.Default)

    override fun onCreate() {
        super.onCreate()
        // Inizializza il CoroutineScope
        scope
    }

    // Metodo che viene chiamato quando viene richiesta la fabbrica di visualizzatori per il widget.
    override fun onGetViewFactory(intent: Intent): RemoteViewsFactory {
        return ListWidgetFactory(applicationContext, intent)
    }




    /* Classe interna ListWidgetFactory implementa l'interfaccia RemoteViewsService.RemoteViewsFactory
        e gestisce il caricamento dei dati per la ListView del widget.
     */
    class ListWidgetFactory(private val context: Context, intent: Intent) : RemoteViewsFactory {

        private val weekHelper = WeekHelpers()
        private val format = "yyyy-dd-MM hh:mm"


        // ID del widget associato alla fabbrica
        private val appWidgetId: Int = intent.getIntExtra(AppWidgetManager.EXTRA_APPWIDGET_ID, AppWidgetManager.INVALID_APPWIDGET_ID)

        // Elenco degli elementi da visualizzare nella ListView del widget
        private lateinit var trackSessionList: ArrayList<TrackSession>

        private val scope = CoroutineScope(Dispatchers.Default) // CoroutineScope all'interno della classe ListWidgetFactory


        override fun onCreate() {
            // Inizializza l'elenco items come vuoto
            trackSessionList = arrayListOf()
            scope.async {
                val range = weekHelper.getWeekRange(System.currentTimeMillis())
                trackSessionList = Datasource(context).getSessionList(range.first, range.second)
            }
        }

        // Ottieni la vista per un determinato elemento della ListView del widget
        override fun getViewAt(position: Int): RemoteViews {
            val remoteViews = RemoteViews(context.packageName, R.layout.list_item_widget)
            val trackSession: TrackSession = trackSessionList[position]

            val itemText = weekHelper.getDate(trackSession.startTime, format) + " | " + DecimalFormat("#.##").format(trackSession.distance/1000) + "km"
            // Imposta il testo dell'elemento corrente nella TextView all'interno dell'elemento della ListView
            remoteViews.setTextViewText(R.id.item_textview, itemText)

            //Imposta intent sul singolo item della lista
            val fillInIntent = Intent()
            //clickIntent.action = "ITEM_CLICK_ACTION"
            //fillInIntent.putExtra(AppWidgetManager.EXTRA_APPWIDGET_ID, appWidgetId)
            // Inserisco Id nell'intent (putExtra FUNZIONA)
            fillInIntent.putExtra("session:id", trackSession.id)
            //https://developer.android.com/reference/android/widget/RemoteViews#setOnClickFillInIntent(int,%20android.content.Intent)
            remoteViews.setOnClickFillInIntent(R.id.list_item_widget, fillInIntent)

            return remoteViews
        }

        // Restituisce il numero di elementi nella ListView del widget
        override fun getCount(): Int {
            return trackSessionList.size
        }

        override fun onDataSetChanged() {
            // Avvia la coroutine per ottenere i dati delle sessioni
            scope.launch {
                // Ottieni lista di TrackSession
                val sessionListDeferred = getSessionsList(context, 0, System.currentTimeMillis())
                //Quando il thread si è concluso imposta valore di items
                trackSessionList = sessionListDeferred.await()
            }
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

        override fun onDestroy() {

        }


        private suspend fun getSessionsList(context: Context, from: Long, to: Long): Deferred<ArrayList<TrackSession>> {
            // Carico dati nel recyclerview in modo asincrono
            Log.d("ListWidgetService", "getSessionsList()")

            return scope.async {
                Datasource(context).getSessionList(from, to)
            }
        }

    }

}

