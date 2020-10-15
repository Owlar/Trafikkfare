package no.hiof.oscarlr.trafikkfare.model

import android.os.Build
import androidx.annotation.RequiresApi
import no.hiof.oscarlr.trafikkfare.R
import java.time.LocalDateTime

data class News(val uid: Int, var road: String, var title: String, var county: String, var description: String, var startDate: LocalDateTime, var endDate: LocalDateTime, var dangerPosterUrl: Int? = null) {

    companion object {
        //To use LocalDateTime
        @RequiresApi(Build.VERSION_CODES.O)
        fun getNews() : ArrayList<News> {

            val newsData = ArrayList<News>()

            val posters = intArrayOf(
                R.drawable.elg,
                R.drawable.sidevind,
                R.drawable.ski,
                R.drawable.rasfare
            )

            val mooseNews = News(
                1, "E6", "Dead moose", "Hedmark", "Truck hit moose, road is blocked",
                LocalDateTime.of(2020, 10, 15, 13, 55), LocalDateTime.now())
            val windNews = News(
                2, "E10", "Storm", "Nordland", "Storm, please stay inside your car",
                LocalDateTime.of(2020, 11, 15, 13, 55), LocalDateTime.now())
            val skiNews = News(
                3, "E45", "Ski festival", "Finnmark", "Ski festival in Finnmark, be aware of skiers",
                LocalDateTime.of(2020, 1, 15, 13, 55), LocalDateTime.now())
            val landslideNews = News(
                4, "E39", "Car rescue", "Sogn og Fjordane", "Rescuing car from landslide. Be careful, there is still a risk of landslides",
                LocalDateTime.of(2020, 4, 15, 14, 55), LocalDateTime.now())

            newsData.add(mooseNews)
            newsData.add(windNews)
            newsData.add(skiNews)
            newsData.add(landslideNews)

            newsData.forEachIndexed { index, News ->
                News.dangerPosterUrl = posters[index]
            }
            return newsData
        }
    }
}