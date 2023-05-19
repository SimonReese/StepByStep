package it.project.appwidget

import android.annotation.SuppressLint
import android.content.Context
import java.text.SimpleDateFormat
import java.util.Calendar

//Crea e restituisce l'array contentente le varie sessioni visualizzabili dall'utente
class Datasource(private val context: Context) {
    //TODO: Crea e ritorna array contenente il risultato della query

    fun getSessionList(): Array<Pair<Int, String>> {
        val trackSessionDao = AppDatabase.getInstance(context).trackSessionDao()
        val pairArray = mutableListOf<Pair<Int, String>>()

        val sessionIdStartTimes: List<TrackSessionDao.SessionIdStartTime> = trackSessionDao.getSessionIdsAndStartTimes()
        for (sessionIdStartTime in sessionIdStartTimes) {
            val sessionId: Int = sessionIdStartTime.id
            val startTime: Long = sessionIdStartTime.startTime
            val format = "yyyy-dd-MM HH:mm:ss"
            val date = getDate(startTime, format)

            val pair = Pair(sessionId, date)
            pairArray.add(pair)

            println("sessionId: $sessionId, startTime: $startTime")
        }

        return pairArray.toTypedArray()
    }


}

//Ritorna data nel formato indicato
fun getDate(milliSeconds: Long, dateFormat: String?): String {
    // Create a DateFormatter object for displaying date in specified format.
    val formatter = SimpleDateFormat(dateFormat)

    // Create a calendar object that will convert the date and time value in milliseconds to date.
    val calendar: Calendar = Calendar.getInstance()
    calendar.timeInMillis = milliSeconds
    return formatter.format(calendar.getTime())
}