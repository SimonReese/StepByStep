package it.project.appwidget

import androidx.lifecycle.LiveData
import androidx.room.Dao
import androidx.room.Delete
import androidx.room.Insert
import androidx.room.Query

@Dao
interface TrackSessionDao {
    @Insert
    fun insertSession(trackSession: TrackSession)

    @Delete
    fun delete(trackSession: TrackSession)

    @Query("UPDATE track_sessions SET activityType = :activityType WHERE id = :sessionId")
    fun updateActivityType(sessionId: Int, activityType: String)

    @Query("SELECT * FROM track_sessions")
    fun getAllTrackSessions(): LiveData<List<TrackSession>>

    @Query("SELECT * FROM track_sessions WHERE id = :sessionId")
    fun getAllTrackSessionsById(sessionId: Int): List<TrackSession>

    @Query("SELECT * FROM track_sessions WHERE startTime >= :startTime AND startTime <= :endTime")
    fun getTrackSessionsBetweenDates(startTime: Long, endTime: Long): LiveData<List<TrackSession>>

    @Query("SELECT id,startTime FROM track_sessions")
    fun getSessionIdsAndStartTimes(): List<SessionIdStartTime>

    // Classe di supporto per rappresentare sessionId e startTime
    data class SessionIdStartTime(
        val id: Int,
        val startTime: Long
    )


}
