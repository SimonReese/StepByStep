package it.project.appwidget

import android.content.Context
import android.util.Log
import androidx.work.Worker
import androidx.work.WorkerParameters

class TrackSessionWorker(context: Context, workerParams: WorkerParameters) : Worker(context, workerParams) {
    override fun doWork(): Result {
        Log.d("TrackSessionWorker", "Avvio worker.")
        Thread.sleep(3000)
        Log.d("TrackSessionWorker", "Fine worker.")
        return Result.success()
    }
}