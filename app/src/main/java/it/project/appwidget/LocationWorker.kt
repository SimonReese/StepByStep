package it.project.appwidget

import android.Manifest
import android.app.PendingIntent
import android.content.Context
import android.content.pm.PackageManager
import android.location.LocationManager
import androidx.core.app.ActivityCompat
import androidx.work.Worker
import androidx.work.WorkerParameters
import android.appwidget.AppWidgetManager
import android.content.Intent
import android.location.Location
import android.util.Log
import android.widget.RemoteViews
import com.google.android.gms.location.LocationRequest
import com.google.android.gms.location.LocationServices

class LocationWorker(private val appContext: Context, workerParams: WorkerParameters) : Worker(appContext, workerParams)
{


    private val context = appContext
    companion object {
        const val ACTION_LOCATION_UPDATE = "com.example.widget.ACTION_LOCATION_UPDATE"
        const val EXTRA_LATITUDE = "com.example.widget.EXTRA_LATITUDE"
        const val EXTRA_LONGITUDE = "com.example.widget.EXTRA_LONGITUDE"
    }

    override fun doWork(): Result {
        return try {
            // Implementa qui la logica per ottenere le informazioni sulla posizione
            // e aggiornare il widget
            val location = getDefaultLocation()
            println(location.latitude)
            println(location.longitude)
            //updateWidget(location)

            Result.success()
        } catch (e: Exception) {
            Result.failure()
        }
    }

    private fun getLocation() {

    }

    private fun getDefaultLocation(): Location {
        val location = Location("dummyprovider")
        location.latitude = 0.0
        location.longitude = 0.0
        return location
    }

    private fun updateWidget(location: Location) {
        val remoteViews = RemoteViews(appContext.packageName, R.layout.small_view_layout)
        //remoteViews.setTextViewText(R.id.locationText, "Lat: ${location.latitude}, Lng: ${location.longitude}")

        val appWidgetManager = AppWidgetManager.getInstance(appContext)
        //val componentName = ComponentName(appContext, MainWidget::class.java)
        //appWidgetManager.updateAppWidget(componentName, remoteViews)
    }

}