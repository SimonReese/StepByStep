package it.project.appwidget.activities

import android.os.Bundle
import android.util.Log
import android.widget.TextView
import androidx.appcompat.app.AppCompatActivity
import androidx.lifecycle.lifecycleScope
import it.project.appwidget.R
import it.project.appwidget.database.AppDatabase
import it.project.appwidget.util.WeekHelpers
import kotlinx.coroutines.launch
import java.text.DecimalFormat


//Activity contenente i dettagli della sessione selezionata
class DetailActivity : AppCompatActivity() {

    // Tag for Log messages
    private val mTAG = this::class.simpleName
    private val weekHelper = WeekHelpers()

    companion object {
        const val ARG_SESSION_ID = "session:id"
    }

    private lateinit var tv_startData: TextView
    private lateinit var tv_endData: TextView
    private lateinit var tv_typeData: TextView
    private lateinit var tv_distanceData: TextView
    private lateinit var tv_timeData: TextView
    private lateinit var tv_avrSpeedData: TextView
    private lateinit var tv_calorieData: TextView


    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        setContentView(R.layout.activity_session_detail)

        // Riferimenti alle Views
        tv_startData = findViewById(R.id.tv_startData)
        tv_endData = findViewById(R.id.tv_endData)
        tv_typeData = findViewById(R.id.tv_typeData)
        tv_distanceData = findViewById(R.id.tv_distanceData)
        tv_timeData = findViewById(R.id.tv_timeData)
        tv_avrSpeedData = findViewById(R.id.tv_avrSpeedData)
        tv_calorieData = findViewById(R.id.tv_caloriesData)


        // Ottengo Id della sessione cliccata
        val sessionId = intent.getIntExtra(ARG_SESSION_ID, -1)

        println("Id ricevuto in DetailActivity: " + sessionId)

        if (sessionId == -1){
            return
        }

        loadData(sessionId)
    }

    override fun onStop() {
        super.onStop()
        Log.v(mTAG, "onStop() called")
        finish()
    }

    override fun onPause() {
        super.onPause()
        Log.v(mTAG, "onPause() called")
    }

    override fun onDestroy() {
        super.onDestroy()
        Log.v(mTAG, "onDestroy() called")
    }

    private fun loadData(sessionId: Int){
        lifecycleScope.launch {
            // Dall'Id ottengo tutte le informazioni sulla sessione
            val trackSessionDao = AppDatabase.getInstance(this@DetailActivity).trackSessionDao()
            val trackSession = trackSessionDao.getTrackSessionById(sessionId)[0]
            val format = "HH:mm:ss"
            val noDecimal = DecimalFormat("#")
            val duration = trackSession.duration/1000

            val hours = duration / 3600
            val minutes = (duration % 3600) / 60
            val seconds = duration % 60

            tv_startData.text = weekHelper.getDate(trackSession.startTime, format)
            tv_endData.text = weekHelper.getDate(trackSession.endTime, format)
            tv_typeData.text = trackSession.activityType
            //aggiunta modifica per la distanza in km
            tv_distanceData.text = noDecimal.format(trackSession.distance/1000) + "km"
            tv_timeData.text = "" + hours + "h " + minutes + "min " + seconds + "sec"
            //aggiunta modifica per la velocità media in km/h
            tv_avrSpeedData.text = (trackSession.averageSpeed * 3.6).toString() + "km/h"
            tv_calorieData.text = (trackSession.kcal).toString() + "Kcal"

        }
    }

}