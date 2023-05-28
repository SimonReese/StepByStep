package it.project.appwidget.util


import java.text.SimpleDateFormat
import java.util.*


/* TODO: Questa classe di fatto non ha variabili membro, ma fornisce metodi utili per
    ricevere i riferimenti alle varie settimane. Probabilmente sarebbe opportuno renderla statica.
 */
class WeekHelpers {

    //  Dato un time in millisecondi fornisce intervallo settimanale in cui esso si trova, ovvero da Lunedì 00:00 a Domenica 23:59 di quella settimana
    fun getWeekRange(timestamp: Long): Pair<Long, Long> {
        val calendar = Calendar.getInstance().apply {
            firstDayOfWeek = Calendar.MONDAY
            timeInMillis = timestamp
        }

        calendar.set(Calendar.DAY_OF_WEEK, Calendar.MONDAY)
        calendar.set(Calendar.HOUR_OF_DAY, 0)
        calendar.set(Calendar.MINUTE, 0)
        calendar.set(Calendar.SECOND, 0)
        calendar.set(Calendar.MILLISECOND, 0)

        val startOfWeek = calendar.timeInMillis

        calendar.set(Calendar.DAY_OF_WEEK, Calendar.SUNDAY)
        calendar.set(Calendar.HOUR_OF_DAY, 23)
        calendar.set(Calendar.MINUTE, 59)
        calendar.set(Calendar.SECOND, 59)

        val endOfWeek = calendar.timeInMillis

        return Pair(startOfWeek, endOfWeek)
    }



    //  Dato un pair in millisecondi mi ritorna l'intervallo della settimana precedente
    fun getPreviousWeekRange(weekRange: Pair<Long, Long>): Pair<Long, Long> {
        val (startOfWeek, endOfWeek) = weekRange

        val startOfPreviousWeek = startOfWeek - 7 * 24 * 60 * 60 * 1000 // Sottrai una settimana in millisecondi
        val endOfPreviousWeek = endOfWeek - 7 * 24 * 60 * 60 * 1000 // Sottrai una settimana in millisecondi

        return Pair(startOfPreviousWeek, endOfPreviousWeek)
    }

    //  Dato un pair in millisecondi mi ritorna l'intervallo della settimana successiva
    fun getNextWeekRange(weekRange: Pair<Long, Long>): Pair<Long, Long> {
        val (startOfWeek, endOfWeek) = weekRange

        val startOfPreviousWeek = startOfWeek + 7 * 24 * 60 * 60 * 1000 // Somma una settimana in millisecondi
        val endOfPreviousWeek = endOfWeek + 7 * 24 * 60 * 60 * 1000 // Somma una settimana in millisecondi

        return Pair(startOfPreviousWeek, endOfPreviousWeek)
    }

    //Ritorna int corrispondente al giorno della settimana (es: 0 = lunedì, 1 = martedì)
    fun getNumberDayOfWeek(timestamp: Long): Int {
        val calendar = Calendar.getInstance()
        calendar.timeInMillis = timestamp

        var dayOfWeek = calendar.get(Calendar.DAY_OF_WEEK) - 2
        if (dayOfWeek < 0) {
            dayOfWeek += 7
        }

        return dayOfWeek
    }

    fun getStringDayOfWeek(time: Long): String {
        return when (getNumberDayOfWeek(time)) {
            0 -> "Lunedì"
            1 -> "Martedì"
            2 -> "Mercoledì"
            3 -> "Giovedì"
            4 -> "Venerdì"
            5 -> "Sabato"
            6 -> "Domenica"
            else -> throw IllegalArgumentException("Numero non valido. Deve essere compreso tra 0 e 6.")
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


}


