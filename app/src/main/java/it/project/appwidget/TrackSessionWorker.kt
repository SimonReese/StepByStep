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
        Log.d("TrackSessionWorker", locationList.toString())
        Log.d("TrackSessionWorker", "Fine worker.")
        return Result.success()
    }
}