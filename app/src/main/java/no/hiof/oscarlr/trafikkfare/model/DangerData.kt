package no.hiof.oscarlr.trafikkfare.model

import no.hiof.oscarlr.trafikkfare.R

data class DangerData(val uid : Int = 0,
                      var title : String = "",
                      var description : String = "",
                      var posterUrl : Int = 0,
                      var latitude : Double = 0.0,
                      var longitude : Double = 0.0
) {

    companion object {
       fun getTestDangers() : ArrayList<DangerData> {
           val data = ArrayList<DangerData>()
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
               val aDanger = DangerData(index, title, "$title is dangerous", posters[index], 59.12478, 11.38754)
               data.add(aDanger)
           }
           return data
       }
    }

}