package it.project.appwidget

import android.content.Intent
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView

class QueryAdapter(private val SessionList: Array<Pair<Int, String>>) :
    RecyclerView.Adapter<QueryAdapter.QueryViewHolder>() {

    private val onClickListener = View.OnClickListener { v ->
        //Passa id alla nuova activity
        val sessionId = v.findViewById<TextView>(R.id.session_id).text
        val intent = Intent(v.context, DetailActivity::class.java)
        intent.putExtra(DetailActivity.ARG_SESSION_ID, sessionId)
        v.context.startActivity(intent)
    }

    // Describes an item view and its place within the RecyclerView
    class QueryViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {
        private val sessionTextView: TextView = itemView.findViewById(R.id.session_text)
        private val sessionId: TextView = itemView.findViewById(R.id.session_id)

        fun bind(pair: Pair<Int, String>) {
            sessionTextView.text = pair.second
            sessionId.text = pair.first.toString()
        }
    }

    // Returns a new ViewHolder
    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): QueryViewHolder {
        val view = LayoutInflater.from(parent.context)
            .inflate(R.layout.session_item, parent, false)

        view.setOnClickListener(onClickListener)

        return QueryViewHolder(view)
    }

    // Returns size of data list
    override fun getItemCount(): Int {
        return SessionList.size
    }

    // Displays data at a certain position
    override fun onBindViewHolder(holder: QueryViewHolder, position: Int) {
        holder.bind(SessionList[position])
    }
}