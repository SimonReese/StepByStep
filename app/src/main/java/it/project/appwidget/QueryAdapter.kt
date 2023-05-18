package it.project.appwidget

import android.content.Intent
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView

class QueryAdapter(private val SessionList: Array<String>) :
    RecyclerView.Adapter<QueryAdapter.QueryViewHolder>() {

    private val onClickListener = View.OnClickListener { v ->
        val flowerName = v.findViewById<TextView>(R.id.session_text).text

        val intent = Intent(v.context, DetailActivity::class.java)
        intent.putExtra(DetailActivity.ARG_SESSION_ID, flowerName)
        v.context.startActivity(intent)
    }

    // Describes an item view and its place within the RecyclerView
    class QueryViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {
        private val flowerTextView: TextView = itemView.findViewById(R.id.session_text)

        fun bind(word: String) {
            flowerTextView.text = word
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