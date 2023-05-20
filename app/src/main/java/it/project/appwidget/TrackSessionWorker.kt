package it.project.appwidget

import android.content.Context
import android.location.Location
import android.util.Log
import androidx.work.Worker
import androidx.work.WorkerParameters
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

        // TODO: usare la classe utility apposita
        // Calcolo media delle velocità e cerco velocità più alta
        var maxSpeed: Float = 0f
        var avgSpeed: Float = 0f
        for (location in locationList){
            if (location.speed > maxSpeed)
                maxSpeed = location.speed
            avgSpeed += location.speed
        }
        avgSpeed = avgSpeed / locationList.size


        // Calcolo valori
        val trackSession = TrackSession(
            startTime = locationList.get(0).time,
            endTime = locationList.last().time,
            duration = locationList.last().time - locationList.last().time, // TODO: passare parametro
            distance = 0.0, //TODO: passare parametro
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