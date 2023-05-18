package it.project.appwidget

import android.content.Context

class Datasource(private val context: Context) {
    //TODO: Crea e ritorna array contenente il risultato della query
    fun getSessionList(): Array<String> {

        // Return flower list from string resources
        return context.resources.getStringArray(R.array.prova_array)
    }
}
