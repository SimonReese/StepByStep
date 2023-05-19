package it.project.appwidget

import android.annotation.SuppressLint
import android.content.Context

class Datasource(private val context: Context) {
    //TODO: Crea e ritorna array contenente il risultato della query
    @SuppressLint("SimpleDateFormat")
    fun getSessionList(): Array<String> {

        val trackSessionDao = AppDatabase.getInstance(context).trackSessionDao()
        val stringArray = mutableListOf<String>()
        val sessionIdStartTimes: List<TrackSessionDao.SessionIdStartTime> = trackSessionDao.getSessionIdsAndStartTimes()
        for (sessionIdStartTime in sessionIdStartTimes)
        {
            val sessionId: Int = sessionIdStartTime.id
            val startTime: Long = sessionIdStartTime.startTime/1000
            val date = java.time.format.DateTimeFormatter.ISO_INSTANT
                .format(java.time.Instant.ofEpochSecond(startTime))
            stringArray.add(sessionId.toString() + " " + date)
            // Puoi fare qualcosa con i valori sessionId e startTime qui
            println("sessionId: $sessionId, startTime: $startTime")
        }

        return stringArray.toTypedArray()
    }
}
