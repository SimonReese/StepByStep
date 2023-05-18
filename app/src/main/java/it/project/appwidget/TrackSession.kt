package it.project.appwidget

import androidx.room.Entity
import androidx.room.PrimaryKey
import java.util.Date

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
