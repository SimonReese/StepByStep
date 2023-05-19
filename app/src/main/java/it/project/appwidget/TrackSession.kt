package it.project.appwidget

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
    val startTime: Long,
    var endTime: Long,
    var duration: Long,
    var distance: Double,
    var averageSpeed: Double,
    var maxSpeed: Double,
    var activityType: String
)
