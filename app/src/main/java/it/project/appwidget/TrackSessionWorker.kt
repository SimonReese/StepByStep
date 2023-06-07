package it.project.appwidget

import android.content.ComponentName
import android.content.Context
import android.content.Intent
import android.location.Location
import android.util.Log
import androidx.work.Worker
import androidx.work.WorkerParameters
import it.project.appwidget.database.AppDatabase
import it.project.appwidget.database.TrackSession
import it.project.appwidget.util.LocationParser
import it.project.appwidget.util.SessionDataProcessor
import it.project.appwidget.widgets.GraphWidget

class TrackSessionWorker(context: Context, workerParams: WorkerParameters) : Worker(context, workerParams) {

    private val kcal_to_m_to_kg_factor: Float = 0.001f //kcal consumate per ogni metro per ogni chilo

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
        var maxSpeed = 0f
        var avgSpeed = 0f
        for (location in locationList){
            // Cerco distanza
            if (location.speed > maxSpeed)
                maxSpeed = location.speed
            // Aggiorno somma velocità
            avgSpeed += location.speed
        }
        avgSpeed = avgSpeed / locationList.size

        // Calcolo la durata totale della sessione
        val duration = locationList.last().time - locationList.first().time

        // TODO: Ricalcolare o leggere dal servizio? Da decidere, nel caso di lettura dal servizio servirà un dato extra
        var distance = 0f
        var index = 0
        while (index < locationList.size -1){
            distance += locationList[index].distanceTo(locationList[index+1])
            index++
        }

        val userPreferencesHelper = UserPreferencesHelper(applicationContext)

        // Calcolo calorico
        var calories = 0
        if (distance.toDouble() > 0){
            calories = (kcal_to_m_to_kg_factor * distance * userPreferencesHelper.peso.toInt()).toInt()
        }

        // Calcolo valori
        val trackSession = TrackSession(
            startTime = locationList[0].time,
            endTime = locationList.last().time,
            duration = duration,
            distance = distance.toDouble(),
            averageSpeed = avgSpeed.toDouble(),
            maxSpeed = maxSpeed.toDouble(),
            activityType = SessionDataProcessor.calculateActivityType(distance, duration),
            kcal = calories
        )

        val db = AppDatabase.getInstance(applicationContext)
        db.trackSessionDao().insertSession(trackSession)
        Log.d("TrackSessionWorker", "Salvataggio sessione $trackSession")

        // Lancio broadcast aggiornamento GraphWidget
        val  graphWidgetIntent = Intent("database-updated")
        graphWidgetIntent.component = ComponentName(applicationContext, GraphWidget::class.java)

        Log.d("TrackSessionWorker", "Fine worker.")
        return Result.success()
    }
}