package it.project.appwidget.database

import androidx.room.Entity
import androidx.room.PrimaryKey
import java.util.Date

/**
 * Entità TrackSession. Memorizza informazioni sul percorso quali ora inizio e fine, distanza, velocità media,
 * velocità massima, tipologia attività
 */
@Entity(tableName = "track_sessions")
data class TrackSession(
    @PrimaryKey(autoGenerate = true) val id: Int = 0,

    /**
     * Ora di inizio attività registrata. Corrisponde alla Unix epoch time della prima location registrata.
     */
    val startTime: Long,

    /**
     * Ora di fine attività registrata. Corrisponde alla Unix epoch time dell'ultima location registrata.
     */
    var endTime: Long,

    /**
     * Durata attività registrata. Corrisponde alla differenza di tempo tra la prima e l'ultima location registrata.
     */
    var duration: Long,

    /**
     * Distanza totale percorsa in metri. Non è la differenza delle distanze tra la prima e l'ultima
     * location, ma piuttosto la somma delle distanze tra ogni location (in genere distanti circa 10m)
     */
    var distance: Double,

    /**
     * TODO
     */
    var averageSpeed: Double,

    /**
     * TODO
     */
    var maxSpeed: Double,

    /**
     * TODO
     */
    var activityType: String
)
