package it.project.appwidget

import android.content.Context
import android.location.Location
import android.util.Log
import androidx.work.Worker
import androidx.work.WorkerParameters
import it.project.appwidget.database.AppDatabase
import it.project.appwidget.database.TrackSession
import it.project.appwidget.util.LocationParser

class TrackSessionWorker(context: Context, workerParams: WorkerParameters) : Worker(context, workerParams) {
    override fun doWork(): Result {
        Log.d("TrackSessionWorker", "Avvio worker.")
        val locationListString: Array<String> = inputData.getStringArray("locationListString") as Array<String>
        val locationList: ArrayList<Location> = ArrayList()
        for (stringLocation in locationListString){
            val location = LocationParser.toLocation(stringLocation)
            locationList.add(location)
        }
        // TODO: usare la classe utility SessionDataProcessor apposita
        // Calcolo media delle velocità e cerco velocità più alta
        var maxSpeed: Float = 0f
        var avgSpeed: Float = 0f
        for (location in locationList){
            // Cerco distanza
            if (location.speed > maxSpeed)
                maxSpeed = location.speed
            // Aggiorno somma velocità
            avgSpeed += location.speed
        }
        avgSpeed = avgSpeed / locationList.size

        // Calcolo la durata totale della sessione
        var duration = locationList.last().time - locationList.first().time

        // TODO: Ricalcolare o leggere dal servizio? Da decidere, nel caso di lettura dal servizio servirà un dato extra
        var distance: Float = 0f
        var index = 0
        while (index < locationList.size -1){
            distance += locationList.get(index).distanceTo(locationList.get(index+1))
            index++
        }

        // Calcolo valori
        val trackSession = TrackSession(
            startTime = locationList.get(0).time,
            endTime = locationList.last().time,
            duration = duration,
            distance = distance.toDouble(),
            averageSpeed = avgSpeed.toDouble(),
            maxSpeed = maxSpeed.toDouble(),
            activityType = "Walking" // TODO: Usare la classe utility apposita
        )

        val db = AppDatabase.getInstance(applicationContext)
        db.trackSessionDao().insertSession(trackSession)
        Log.d("TrackSessionWorker", "Salvataggio sessione ${trackSession}")

        Log.d("TrackSessionWorker", "Fine worker.")
        return Result.success()
    }
}