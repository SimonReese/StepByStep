package it.project.appwidget

import androidx.lifecycle.LiveData
import androidx.room.Dao
import androidx.room.Delete
import androidx.room.Insert
import androidx.room.Query

@Dao
interface TrackSessionDao {

    /**
     * Inserisce nuovo oggetto TrackSession nel database
     */
    @Insert
    fun insertSession(trackSession: TrackSession)

    /**
     * Rimuove oggetto TrackSession dal database
     */
    @Delete
    fun delete(trackSession: TrackSession)

    /**
     * Aggiorna oggetto TrackSession nel database
     */
    @Query("UPDATE track_sessions SET activityType = :activityType WHERE id = :sessionId")
    fun updateActivityType(sessionId: Int, activityType: String)

    /**
     * Restituisce tutti gli oggetti TrackSession nel database
     */
    @Query("SELECT * FROM track_sessions")
    fun getAllTrackSessions(): LiveData<List<TrackSession>>

    /**
     * Restituisce tutti gli oggetti TrackSession nel database tramite id
     */
    @Query("SELECT * FROM track_sessions WHERE id = :sessionId")
    fun getTrackSessionById(sessionId: Int): List<TrackSession>

    /**
     * Restituisce tutti gli oggetti TrackSession nel database tra due date
     */
    @Query("SELECT * FROM track_sessions WHERE startTime >= :startTime AND startTime <= :endTime")
    fun getTrackSessionsBetweenDates(startTime: Long, endTime: Long): List<TrackSession>

    /**
     * ? //TODO: è utile?
     */
    @Query("SELECT id,startTime FROM track_sessions")
    fun getSessionIdsAndStartTimes(): List<SessionIdStartTime>

    // Classe di supporto per rappresentare sessionId e startTime
    data class SessionIdStartTime(
        val id: Int,
        val startTime: Long
    )


}
