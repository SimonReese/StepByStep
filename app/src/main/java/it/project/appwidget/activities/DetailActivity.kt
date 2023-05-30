package it.project.appwidget.activities

import android.os.Bundle
import android.util.Log
import android.widget.TextView
import androidx.appcompat.app.AppCompatActivity
import it.project.appwidget.R
import it.project.appwidget.database.AppDatabase
import it.project.appwidget.util.WeekHelpers

class DetailActivity : AppCompatActivity() {

    // Tag for Log messages
    private val mTAG = this::class.simpleName
    private val weekHelper = WeekHelpers()

    companion object {
        // The activity argument representing session name
        const val ARG_SESSION_ID = "session:id"
    }

    // Views
    private lateinit var tv_startData: TextView
    private lateinit var tv_endData: TextView
    private lateinit var tv_typeData: TextView
    private lateinit var tv_distanceData: TextView
    private lateinit var tv_timeData: TextView
    private lateinit var tv_avrSpeedData: TextView

    // Called when the activity is first created
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        // Display the layout
        setContentView(R.layout.activity_session_detail)

        // Riferimenti alle Views
        tv_startData = findViewById(R.id.tv_startData)
        tv_endData = findViewById(R.id.tv_endData)
        tv_typeData = findViewById(R.id.tv_typeData)
        tv_distanceData = findViewById(R.id.tv_distanceData)
        tv_timeData = findViewById(R.id.tv_timeData)
        tv_avrSpeedData = findViewById(R.id.tv_avrSpeedData)

        // Ottengo Id della sessione cliccata
        val sessionId = intent.getIntExtra(ARG_SESSION_ID, -1)
        // dall'Id ottiene tutte le informazioni della sessione
        val trackSessionDao = AppDatabase.getInstance(this).trackSessionDao()

        println("Id ricevuto in DetailActivity: " + sessionId)

        if (sessionId != -1)
        {
            try {
                val session = trackSessionDao.getTrackSessionById(sessionId!!.toInt())[0]

                val format = "yyyy-dd-MM HH:mm:ss"

                val startTime = session.startTime
                val endTime = session.endTime
                val type = session.activityType
                val distance = session.distance
                val time = session.duration
                val avrSpeed = session.averageSpeed



                val startDate = weekHelper.getDate(startTime, format)
                val endDate = weekHelper.getDate(endTime, format)



                tv_startData.text = startDate


                tv_endData.text = endDate


                tv_typeData.text = type


                tv_distanceData.text = distance.toString()


                tv_timeData.text = time.toString()


                tv_avrSpeedData.text = avrSpeed.toString()

            }
            catch (e: NullPointerException)
            {
                Log.d("NullPointerException", "ERRORE")
            }

        }


    }

    override fun onDestroy()
    {
        super.onDestroy()
        Log.v(mTAG, "onDestroy() called")
    }


}