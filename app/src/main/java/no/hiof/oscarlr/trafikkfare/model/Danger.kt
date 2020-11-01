package no.hiof.oscarlr.trafikkfare.model

import com.google.android.gms.maps.model.BitmapDescriptorFactory
import com.google.android.gms.maps.model.LatLng
import no.hiof.oscarlr.trafikkfare.MapActivity
import no.hiof.oscarlr.trafikkfare.R

data class Danger(val uid : Int, var title : String, var description : String, var posterUrl : Int, var position : LatLng) {

    companion object {

       fun getTestDangers() : ArrayList<Danger> {
           val data = ArrayList<Danger>()
           val posters = intArrayOf(
               R.drawable.elg,
               R.drawable.sidevind,
               R.drawable.ski,
               R.drawable.rasfare
           )
           val titles = arrayOf(
               "Elg",
               "Sidevind",
               "Skiløpere",
               "Rasfare"
           )
           titles.forEachIndexed { index, title ->
               val aDanger = Danger(index, title, "$title is dangerous", posters[index], LatLng(59.12478, 11.38754))
               data.add(aDanger)
           }
           return data
       }

        private var dangers = ArrayList<Danger>()

        fun getDangers() : ArrayList<Danger> {
            return dangers
        }

        fun setDangers(dangersToSet: ArrayList<Danger>) {
            dangersToSet.forEach {
                if (!dangers.contains(it))
                    dangers.add(it)
            }
        }

    }

}