package it.project.appwidget

import android.content.Context
import android.location.Location
import java.util.Date

// Classe per elaborare i dati della sessione
class SessionDataProcessor {
    companion object {
        fun calculateActivityType(distance: Float, duration: Long): String {
            // Calcolo del tipo di attività in base alla distanza e alla durata
            // TODO: calcolarlo anche in base alla velocità media
            return when {
                distance > 5000 && duration > 1800000 -> "Running"
                distance > 1000 && duration > 900000 -> "Cycling"
                else -> "Walking"
            }
        }

        fun calculateAverageSpeed(distance: Float, duration: Long): Double {
            // Calcola la velocità media in base alla distanza e alla durata
            return if (duration > 0) {
                (distance / duration)*1000.toDouble()
            } else {
                0.0
            }
        }

        fun calculateMaxSpeed(speed: Float): Double {
            // Calcola la velocità massima confrontando la velocità massima attuale con la velocità corrente
            return speed.toDouble()
        }
    }
}
