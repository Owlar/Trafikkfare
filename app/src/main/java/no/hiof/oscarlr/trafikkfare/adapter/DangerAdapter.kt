package no.hiof.oscarlr.trafikkfare.adapter

import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import kotlinx.android.synthetic.main.danger_list_item.view.*
import no.hiof.oscarlr.trafikkfare.R
import no.hiof.oscarlr.trafikkfare.model.Danger

/* Source: https://github.com/larseknu/mobilprogrammering2019/tree/master/Lecture05_RecyclerView/AfterLecture_Kotlin */
class DangerAdapter (private val items: ArrayList<Danger>, var clickListener: View.OnClickListener) : RecyclerView.Adapter<DangerAdapter.DangerViewHolder>() {
    override fun getItemCount(): Int {
        return items.size
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): DangerViewHolder {
        Log.d("DangerAdapter", "Creating View")
        val itemView = LayoutInflater.from(parent.context).inflate(R.layout.danger_list_item, parent, false)
        return DangerViewHolder(itemView)
    }

    override fun onBindViewHolder(holder: DangerViewHolder, position: Int) {
        Log.d("DangerAdapter", "Binding View $position")
        val danger = items[position]
        holder.bind(danger, clickListener)
    }

    class DangerViewHolder (view: View) : RecyclerView.ViewHolder(view) {
        private val dangerPosterImageView : ImageView = view.dangerPosterImageView
        private val dangerTitleTextView : TextView = view.dangerTitleTextView

        fun bind(item: Danger, clickListener: View.OnClickListener) {
            dangerPosterImageView.setImageResource(item.posterUrl)
            dangerTitleTextView.text = item.title
            this.itemView.setOnClickListener(clickListener)
        }

    }
}
