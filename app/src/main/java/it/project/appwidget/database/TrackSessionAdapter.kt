package it.project.appwidget.database

import android.content.Intent
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import it.project.appwidget.activities.DetailActivity
import it.project.appwidget.R
import java.text.DecimalFormat
import java.text.SimpleDateFormat

// TODO: Ri-organizzare layout della lista
class TrackSessionAdapter(private val trackSessionList: ArrayList<TrackSession>) :
    RecyclerView.Adapter<TrackSessionAdapter.TrackSessionViewHolder>() {

    // TODO: Impostare listener tramite parametro su costruttore
    private val onClickListener = View.OnClickListener { v ->
        //Passa id alla nuova activity
        val sessionId = v.findViewById<TextView>(R.id.trackSessionIdTextView).text
        val intent = Intent(v.context, DetailActivity::class.java)
        intent.putExtra(DetailActivity.ARG_SESSION_ID, sessionId)
        v.context.startActivity(intent)
    }

    // Describes an item view and its place within the RecyclerView
    class TrackSessionViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {

        // Views associate all'item
        private val trackSessionIdTextView: TextView = itemView.findViewById(R.id.trackSessionIdTextView)
        private val trackSessionDateTextView: TextView = itemView.findViewById(R.id.trackSessionDateTextView)
        private val trackSessionTimeTextView: TextView = itemView.findViewById(R.id.trackSessionTimeTextView)
        private val trackSessionDistanceTextView: TextView = itemView.findViewById(R.id.trackSessionDistanceTextView)

        fun bind(trackSession: TrackSession) {
            trackSessionIdTextView.text = trackSession.id.toString()
            val dateFormat = SimpleDateFormat("dd/MM")
            val hourFormat = SimpleDateFormat("HH:mm")
            val distanceFormat = DecimalFormat("##.#")
            trackSessionDateTextView.text = dateFormat.format(trackSession.startTime)
            trackSessionTimeTextView.text = hourFormat.format(trackSession.startTime)
            trackSessionDistanceTextView.text = distanceFormat.format(trackSession.distance / 1000) + "km"
        }
    }

    // Returns a new ViewHolder
    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): TrackSessionViewHolder {
        val view = LayoutInflater.from(parent.context)
            .inflate(R.layout.tracksession_item, parent, false)

        view.setOnClickListener(onClickListener)

        return TrackSessionViewHolder(view)
    }

    // Returns size of data list
    override fun getItemCount(): Int {
        return trackSessionList.size
    }

    // Displays data at a certain position
    override fun onBindViewHolder(holder: TrackSessionViewHolder, position: Int) {
        holder.bind(trackSessionList[position])
    }
}