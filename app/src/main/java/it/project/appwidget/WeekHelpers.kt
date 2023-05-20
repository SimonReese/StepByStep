package it.project.appwidget


import java.util.*

class WeekHelpers {

    //  Dato un time in millisecondi fornisce intervallo settimanale in cui esso si trova, ovvero da Lunedì 00:00 a Domenica 23:59 di quella settimana
    fun getWeekRange(timestamp: Long): Pair<Long, Long> {
        val calendar = Calendar.getInstance()
        calendar.timeInMillis = timestamp

        // Imposta il calendario al Lunedì della settimana corrente
        calendar.set(Calendar.DAY_OF_WEEK, Calendar.MONDAY)
        calendar.set(Calendar.HOUR_OF_DAY, 0)
        calendar.set(Calendar.MINUTE, 0)
        calendar.set(Calendar.SECOND, 0)
        calendar.set(Calendar.MILLISECOND, 0)

        val startOfWeek = calendar.timeInMillis

        // Imposta il calendario alla Domenica della settimana corrente
        calendar.add(Calendar.DATE, 6)
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

    //  Ritorna il giorno della settimana (0 = lunedì, 1 = martedì, etc)
    //  TODO: lo possiamo utilizzare per il graph in un ciclo for per cui per ogni elemento di un array di session della settimana x
    //  viene calcolato il giorno e poi la distanza percorsa viene sommata al contenuto dell'array<Int> di quel determinato giorno.
    //  Questo array viene poi utilizzato per creare il grafico

    fun getDayOfWeek(timestamp: Long): Int {
        val calendar = Calendar.getInstance()
        calendar.timeInMillis = timestamp

        var dayOfWeek = calendar.get(Calendar.DAY_OF_WEEK) - 2
        if (dayOfWeek < 0) {
            dayOfWeek += 7
        }

        return dayOfWeek
    }


}
