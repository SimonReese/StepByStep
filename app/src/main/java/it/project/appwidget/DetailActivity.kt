package it.project.appwidget

import android.os.Bundle
import android.util.Log
import android.widget.TextView
import androidx.appcompat.app.AppCompatActivity

class DetailActivity : AppCompatActivity()
{
    // Tag for Log messages
    private val mTAG = this::class.simpleName

    // Called when the activity is first created
    override fun onCreate(savedInstanceState: Bundle?)
    {
        super.onCreate(savedInstanceState)

        // Display the layout
        setContentView(R.layout.activity_session_detail)

        // Get the reference to the TextView
        val tv : TextView = findViewById(R.id.textView)

        // Set the message in the TextView
        val flowerName = intent.getStringExtra(ARG_SESSION_ID)
        tv.text = flowerName
    }

    override fun onDestroy()
    {
        super.onDestroy()
        Log.v(mTAG, "onDestroy() called")
    }

    companion object {
        // The activity argument representing session name
        const val ARG_SESSION_ID = "session:id"
    }
}