package it.project.appwidget

import android.content.Context
import it.project.appwidget.database.AppDatabase
import it.project.appwidget.database.TrackSession
import java.text.SimpleDateFormat
import java.util.Calendar

//Crea e restituisce l'array contentente le varie sessioni visualizzabili dall'utente
class Datasource(private val context: Context) {

    private val weekHelper = WeekHelpers()


    // Crea e ritorna array contenente il risultato della query getSessionIdsAndStartTimes
    fun getSessionListIdString(from: Long, to: Long): Array<Pair<Int, String>> {
        val trackSessionDao = AppDatabase.getInstance(context).trackSessionDao()
        val pairArray = mutableListOf<Pair<Int, String>>()
        val sessionIdStartTimes: List<TrackSession> = trackSessionDao.getTrackSessionsBetweenDates(from, to)

        for (sessionIdStartTime in sessionIdStartTimes) {
            val sessionId: Int = sessionIdStartTime.id
            val startTime: Long = sessionIdStartTime.startTime
            val dayNum = weekHelper.getNumberDayOfWeek(startTime)
            val dayStr = weekHelper.getStringDayOfWeek(dayNum)
            val format = "HH:mm"
            val date = getDate(startTime, format)

            val pair = Pair(sessionId, dayStr + ": " + date)
            pairArray.add(pair)

            println("sessionId: $sessionId, startTime: $startTime")
        }

        return pairArray.toTypedArray()
    }

    fun getSessionList(from: Long, to: Long): List<TrackSession> {
        val trackSessionDao = AppDatabase.getInstance(context).trackSessionDao()
        return trackSessionDao.getTrackSessionsBetweenDates(from, to)
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