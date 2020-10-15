package no.hiof.oscarlr.trafikkfare.adapter

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import kotlinx.android.synthetic.main.fragment_home_news_list_item.view.*
import no.hiof.oscarlr.trafikkfare.R
import no.hiof.oscarlr.trafikkfare.model.News

class NewsAdapter(private val items : ArrayList<News>) : RecyclerView.Adapter<NewsAdapter.NewsViewHolder>() {
    override fun getItemCount(): Int {
        return items.count()
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): NewsViewHolder {
        return NewsViewHolder(LayoutInflater.from(parent.context).inflate(R.layout.fragment_home_news_list_item, parent, false))
    }

    override fun onBindViewHolder(holder: NewsViewHolder, position: Int) {
        holder.bind(items[position])
    }

    class NewsViewHolder (view: View) : RecyclerView.ViewHolder(view) {
        private val newsPosterImageView = view.news_item_image
        private val newsRoadTextView = view.news_item_road
        private val newsTitleTextView = view.news_item_title
        private val newsCountyTextView = view.news_item_county
        private val newsDescriptionTextView = view.news_item_description
        private val newsStartDateTextView = view.news_item_startDate
        private val newsEndDateTextView = view.news_item_endDate

        fun bind(item: News) {
            item.dangerPosterUrl?.let { newsPosterImageView.setImageResource(it) }
            newsRoadTextView.text = item.road
            newsTitleTextView.text = item.title
            newsCountyTextView.text = item.county
            newsDescriptionTextView.text = item.description
            newsStartDateTextView.text = item.startDate.toString()
            newsEndDateTextView.text = item.endDate.toString()
        }
    }

}