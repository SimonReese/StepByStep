package it.project.appwidget

import android.appwidget.AppWidgetManager
import android.content.Context
import android.content.Intent
import android.util.Log
import android.widget.RemoteViews
import android.widget.RemoteViewsService
import androidx.lifecycle.LifecycleCoroutineScope
import it.project.appwidget.database.TrackSession
import it.project.appwidget.util.WeekHelpers
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Deferred
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.async
import kotlinx.coroutines.launch
import java.text.DecimalFormat

// La classe ListWidgetService è responsabile di fornire una fabbrica di visualizzatori per il widget

class ListWidgetService : RemoteViewsService() {

    override fun onCreate() {
        super.onCreate()
        Log.d("ListWidgetService", "Chiamato onCreate()")
    }

    // Metodo che viene chiamato quando viene richiesta la fabbrica di views per il widget.
    override fun onGetViewFactory(intent: Intent): RemoteViewsFactory {
        return ListWidgetFactory(applicationContext, intent)
    }


    /* Classe interna ListWidgetFactory implementa l'interfaccia RemoteViewsService.RemoteViewsFactory
        e gestisce il caricamento dei dati per la ListView del widget. */
    class ListWidgetFactory(private val context: Context, private val intent: Intent) : RemoteViewsFactory {

        private val weekHelper = WeekHelpers()
        private val format = "yyyy-dd-MM hh:mm"

        // Lista degli elementi da visualizzare nella ListView del widget
        private lateinit var trackSessionList: ArrayList<TrackSession>
        // Id widget di riferimento
        private var appWidgetId: Int = -1
        // Range della settimana di riferimento
        private lateinit var weekRange: Pair<Long, Long>


        override fun onCreate() {
            Log.d("ListWidgetFatory", "Chiamato onCreate()")
            // Inizializza l'elenco items come vuoto
            trackSessionList = arrayListOf()
            // Recupero widgetID dall' intent
            appWidgetId = intent.getIntExtra(AppWidgetManager.EXTRA_APPWIDGET_ID, -1)
        }

        /* Una volta che ListWidgetFactory viene creato, o quando viene aggiornato, viene chiamato
        il seguente metodo per popolare la RemoteView. Non è necessario avviare una coroutine perchè
         il sistema continuerà a mostrare il dataset precedente finchè il nuovo dato non è pronto.*/
        override fun onDataSetChanged() {
            Log.d("ListWidgetFactory", "Chiamato onDatasetChanged()")
            // Recupero sharedPreferences
            val preferencesFileName = "it.project.appwidget.listwidget." + appWidgetId
            val sharedPreferences = context.getSharedPreferences(preferencesFileName, Context.MODE_PRIVATE)
            // Cerco il primo valore di weekRange eventualmente salvato
            var startRange =  sharedPreferences.getLong("range.first", -1L)

            if (startRange == -1L){
                Log.d("ListWidgetFactory", "Range non trovato. Imposto range settimana corrente.")
                startRange = System.currentTimeMillis()
            }
            // Aggiorno valore weekRange
            weekRange = weekHelper.getWeekRange(startRange)

            // Leggo entries dal database in base alla settimana selezionata e aggiorno dati
            trackSessionList = Datasource(context).getSessionList(weekRange.first, weekRange.second)
        }

        // Restituisce il numero di elementi nella ListView del widget
        override fun getCount(): Int {
            Log.d("ListWidgetFactory", "Chiamato getCount()")
            return trackSessionList.size
        }

        // Restituisce il numero di tipi di visualizzatori diversi
        override fun getViewTypeCount(): Int {
            return 1
        }

        // Volendo, restituisce una view di caricamento personalizzata. In questo caso restituisce null e viene utilizzata la view di default
        override fun getLoadingView(): RemoteViews? {
            return null
        }

        // Ottieni la view per un determinato elemento della ListView del widget
        override fun getViewAt(position: Int): RemoteViews {
            Log.d("ListWidgetFactory", "Chiamato getViewAt()")
            val remoteViews = RemoteViews(context.packageName, R.layout.list_item_widget)
            val trackSession: TrackSession = trackSessionList[position]


            //modificare qui il codice
            //TODO finire le modifiche del codice
            val itemText = weekHelper.getDate(trackSession.startTime, format) + " | " + DecimalFormat("#.##").format(trackSession.distance/1000) + "km" +
                    " | " + DecimalFormat("#.##").format(trackSession.averageSpeed) + "km/h"
            // Imposta il testo dell'elemento corrente nella TextView all'interno dell'elemento della ListView
            remoteViews.setTextViewText(R.id.item_textview, itemText)

            //Imposta intent sul singolo item della lista
            val fillInIntent = Intent()
            // Inserisco Id sessione nell'intent
            fillInIntent.putExtra("session:id", trackSession.id)
            //https://developer.android.com/reference/android/widget/RemoteViews#setOnClickFillInIntent(int,%20android.content.Intent)
            remoteViews.setOnClickFillInIntent(R.id.list_item_widget, fillInIntent)

            return remoteViews
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
            Log.d("ListWidgetFactory", "Chiamato onDestroy()")
        }

    }

}

