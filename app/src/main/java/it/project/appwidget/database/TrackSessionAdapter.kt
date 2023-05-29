package it.project.appwidget.database

import android.content.Intent
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import it.project.appwidget.activities.DetailActivity
import it.project.appwidget.R

// TODO: Ri-organizzare layout della lista
class TrackSessionAdapter(private val sessionList: Array<Pair<Int, String>>) :
    RecyclerView.Adapter<TrackSessionAdapter.SessionViewHolder>() {

    private val onClickListener = View.OnClickListener { v ->
        //Passa id alla nuova activity
        val sessionId = v.findViewById<TextView>(R.id.session_id).text
        val intent = Intent(v.context, DetailActivity::class.java)
        intent.putExtra(DetailActivity.ARG_SESSION_ID, sessionId)
        v.context.startActivity(intent)
    }

    // Describes an item view and its place within the RecyclerView
    class SessionViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {
        private val sessionTextView: TextView = itemView.findViewById(R.id.session_text)
        private val sessionId: TextView = itemView.findViewById(R.id.session_id)

        fun bind(pair: Pair<Int, String>) {
            sessionTextView.text = pair.second
            sessionId.text = pair.first.toString()
        }
    }

    // Returns a new ViewHolder
    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): SessionViewHolder {
        val view = LayoutInflater.from(parent.context)
            .inflate(R.layout.tracksession_item, parent, false)

        view.setOnClickListener(onClickListener)

        return SessionViewHolder(view)
    }

    // Returns size of data list
    override fun getItemCount(): Int {
        return sessionList.size
    }

    // Displays data at a certain position
    override fun onBindViewHolder(holder: SessionViewHolder, position: Int) {
        holder.bind(sessionList[position])
    }
}