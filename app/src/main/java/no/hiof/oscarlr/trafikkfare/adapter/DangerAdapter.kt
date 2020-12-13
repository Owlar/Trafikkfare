package no.hiof.oscarlr.trafikkfare.adapter

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import kotlinx.android.synthetic.main.danger_list_item.view.*
import no.hiof.oscarlr.trafikkfare.R
import no.hiof.oscarlr.trafikkfare.model.DangerCollectionData

class DangerAdapter (private val items: ArrayList<DangerCollectionData>, private var clickListener: View.OnClickListener) : RecyclerView.Adapter<DangerAdapter.DangerViewHolder>() {
    override fun getItemCount(): Int {
        return items.size
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): DangerViewHolder {
        return DangerViewHolder(LayoutInflater.from(parent.context).inflate(R.layout.danger_list_item, parent, false))
    }

    override fun onBindViewHolder(holder: DangerViewHolder, position: Int) {
        val danger = items[position]
        holder.bind(danger, clickListener)
    }

    class DangerViewHolder (view: View) : RecyclerView.ViewHolder(view) {
        private val dangerPosterImageView = view.dangerPosterImageView
        private val dangerTitleTextView = view.dangerTitleTextView

        fun bind(item: DangerCollectionData, clickListener: View.OnClickListener) {
            dangerPosterImageView.setImageResource(item.posterUrl)
            dangerTitleTextView.text = item.title
            this.itemView.setOnClickListener(clickListener)
        }

    }
}
