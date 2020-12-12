package no.hiof.oscarlr.trafikkfare.model

import android.os.Build
import androidx.annotation.RequiresApi
import no.hiof.oscarlr.trafikkfare.R
import java.time.LocalDateTime

data class News(var title: String,
                var description: String,
                var road: String,
                var county: String,
                var startDate: LocalDateTime,
                var dangerPosterUrl: Int? = null
) {

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

            val mooseNews = News("Død elg", "Lastebilsjåfør kolliderte med elg, vei blokkert.", "E6", "Hedmark",
                LocalDateTime.now())
            val windNews = News("Storm", "Det blåser noe voldsomt, ikke gå ut av bilen.", "E10", "Nordland",
                LocalDateTime.now())
            val skiNews = News("Ski festival", "Ski festival i Finnmark, kjør forsiktig og vær obs på skiløpere.", "E45", "Finnmark",
                LocalDateTime.now())
            val landslideNews = News("Bilberging", "Bilberging etter ras. Vær forsiktig, det er fortsatt rasfare i nærheten av ulykken.", "E39", "Sogn og Fjordane",
                LocalDateTime.now())

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