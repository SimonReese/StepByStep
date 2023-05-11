package it.project.appwidget

import android.Manifest
import android.content.Context
import android.content.pm.PackageManager
import android.location.LocationManager
import androidx.core.app.ActivityCompat
import androidx.work.Data
import androidx.work.Worker
import androidx.work.WorkerParameters


class LocationWorker(appContext: Context, workerParams: WorkerParameters) : Worker(appContext, workerParams) {

    private val context = appContext.applicationContext

    override fun doWork(): Result {
        // Ottieni la posizione attuale
        val locationManager = context.getSystemService(Context.LOCATION_SERVICE) as LocationManager

        //Controlla permessi
        if (ActivityCompat.checkSelfPermission(
                context,
                Manifest.permission.ACCESS_FINE_LOCATION
            ) != PackageManager.PERMISSION_GRANTED && ActivityCompat.checkSelfPermission(
                context,
                Manifest.permission.ACCESS_COARSE_LOCATION
            ) != PackageManager.PERMISSION_GRANTED
        ) {
            // TODO: Consider calling
            //    ActivityCompat#requestPermissions
            // here to request the missing permissions, and then overriding
            //   public void onRequestPermissionsResult(int requestCode, String[] permissions,
            //                                          int[] grantResults)
            // to handle the case where the user grants the permission. See the documentation
            // for ActivityCompat#requestPermissions for more details.
            println("NO PERMESSI")
            return Result.failure()
        }
        else
        {
            println("SI PERMESSI")
        }


        val location = locationManager.getLastKnownLocation(LocationManager.GPS_PROVIDER)
        locationManager.getLastKnownLocation(LocationManager.GPS_PROVIDER)

        // Ottenere la velocità attuale dalla posizione
        val speed = location?.speed ?: 0f

        // Ottieni la latitudine e la longitudine dalla posizione
        val latitude = location?.latitude ?: 0.0
        val longitude = location?.longitude ?: 0.0

        println(speed)
        println(latitude)
        println(longitude)

        // Restituisci la velocità come risultato del lavoro
        val outputData = Data.Builder()
            .putDouble("current_latitude", latitude)
            .putDouble("current_longitude", longitude)
            .putFloat("current_speed", speed)
            .build()
        return Result.success(outputData)
    }

}

