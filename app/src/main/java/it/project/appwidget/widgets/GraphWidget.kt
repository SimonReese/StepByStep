package it.project.appwidget.widgets

import android.appwidget.AppWidgetManager
import android.appwidget.AppWidgetProvider
import android.content.Context
import android.graphics.Bitmap
import android.util.Log
import android.util.SizeF
import android.widget.RemoteViews
import it.project.appwidget.BarChart
import it.project.appwidget.R
import it.project.appwidget.activities.deleteTitlePref
import it.project.appwidget.activities.loadTitlePref

/**
 * Implementazione GraphWidget, che mostra un grafico rispetto alla proprietà configurata (default: distanza).
 * Sequenza di chiamate: onEnabled() -> onReceive() -> onUpdate() | onAppWidgetOptionsChanged() -> onDeleted() -> onDisabled().
 * App Widget Configuration implemented in [it.project.appwidget.activities.GraphWidgetConfigureActivity]
 */
class GraphWidget : AppWidgetProvider() {

    override fun onEnabled(context: Context) {
        Log.d("GraphWidget", "Chiamato onEnabled")
    }

    override fun onReceive(context: Context?, intent: Intent?) {
        Log.d("GraphWidget", "Chiamato onReceive")
        super.onReceive(context, intent)
    }

    override fun onUpdate(context: Context, appWidgetManager: AppWidgetManager, appWidgetIds: IntArray) {
        Log.d("GraphWidget", "Chiamato onUpdate")
        // There may be multiple widgets active, so update all of them
        for (appWidgetId in appWidgetIds) {
            updateWidget(context, appWidgetManager, appWidgetId)
        }
    }

    override fun onAppWidgetOptionsChanged(
        context: Context?,
        appWidgetManager: AppWidgetManager?,
        appWidgetId: Int,
        newOptions: Bundle?
    ) {
        super.onAppWidgetOptionsChanged(context, appWidgetManager, appWidgetId, newOptions)
        Log.d("GraphWidget", "Chiamato onAppWidgetOptionsChanged")
    }

    override fun onDeleted(context: Context, appWidgetIds: IntArray) {
        Log.d("GraphWidget", "Chiamato onDeleted")
        // When the user deletes the widget, delete the preference associated with it.
        for (appWidgetId in appWidgetIds) {
            deleteTitlePref(context, appWidgetId)
        }
    }

    override fun onDisabled(context: Context) {
        Log.d("GraphWidget", "Chiamato onDisabled")
    }

    // Aggiornamento del singolo widget
    private fun updateWidget(context: Context, appWidgetManager: AppWidgetManager, appWidgetId: Int) {
        //val widgetText = loadTitlePref(context, appWidgetId)

        // Ottengo rifetrimento alle RemoteViews
        val views = RemoteViews(context.packageName, R.layout.graph_widget)

        // Ottengo dimensioni widget
        val options = appWidgetManager.getAppWidgetOptions(appWidgetId)
        val keys = options.keySet()
        for (key in keys){
            println("Ricevute opzioni ${key}")
        }


        // Costruisco grafico
        val chart = BarChart(context, null)
        val image: Bitmap = chart.getChartImage()

        // Imposto immagine nella viewImage
        views.setImageViewBitmap(R.id.graphImageView, image)

        // Instruct the widget manager to update the widget
        appWidgetManager.updateAppWidget(appWidgetId, views)
    }
}

