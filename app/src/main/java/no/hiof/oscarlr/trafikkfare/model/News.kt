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
                1, "E6", "Død elg", "Hedmark", "Lastebilsjåfør kolliderte med elg, vei blokkert.",
                LocalDateTime.of(2020, 10, 15, 13, 55), LocalDateTime.now())
            val windNews = News(
                2, "E10", "Storm", "Nordland", "Det blåser noe voldsomt, ikke gå ut av bilen.",
                LocalDateTime.of(2020, 11, 15, 13, 55), LocalDateTime.now())
            val skiNews = News(
                3, "E45", "Ski festival", "Finnmark", "Ski festival i Finnmark, kjør forsiktig og vær obs på skiløpere.",
                LocalDateTime.of(2020, 1, 15, 13, 55), LocalDateTime.now())
            val landslideNews = News(
                4, "E39", "Car rescue", "Sogn og Fjordane", "Bilberging etter ras. Vær forsiktig, det er fortsatt rasfare i nærheten av ulykken.",
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