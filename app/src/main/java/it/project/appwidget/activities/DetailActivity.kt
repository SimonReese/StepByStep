package it.project.appwidget.activities

import android.os.Bundle
import android.util.Log
import android.widget.TextView
import androidx.appcompat.app.AppCompatActivity
import it.project.appwidget.R
import it.project.appwidget.database.AppDatabase
import it.project.appwidget.util.WeekHelpers

class DetailActivity : AppCompatActivity()
{
    // Tag for Log messages
    private val mTAG = this::class.simpleName
    private val weekHelper = WeekHelpers()

    companion object {
        // The activity argument representing session name
        const val ARG_SESSION_ID = "session:id"
    }


    // Called when the activity is first created
    override fun onCreate(savedInstanceState: Bundle?)
    {
        super.onCreate(savedInstanceState)

        // Display the layout
        setContentView(R.layout.activity_session_detail)

        // ottiene Id della sessione cliccata
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


                val tv_startData: TextView = findViewById(R.id.tv_startData)
                tv_startData.text = startDate

                val tv_endData: TextView = findViewById(R.id.tv_endData)
                tv_endData.text = endDate

                val tv_typeData: TextView = findViewById(R.id.tv_typeData)
                tv_typeData.text = type

                val tv_distanceData: TextView = findViewById(R.id.tv_distanceData)
                tv_distanceData.text = distance.toString()

                val tv_timeData: TextView = findViewById(R.id.tv_timeData)
                tv_timeData.text = time.toString()

                val tv_avrSpeedData: TextView = findViewById(R.id.tv_avrSpeedData)
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