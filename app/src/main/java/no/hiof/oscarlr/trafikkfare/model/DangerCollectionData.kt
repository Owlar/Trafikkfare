package no.hiof.oscarlr.trafikkfare.model

import no.hiof.oscarlr.trafikkfare.R

data class DangerCollectionData(val uid : Int = 0,
                                var title : String = "",
                                var description : String = "",
                                var posterUrl : Int = 0,
                                var latitude : Double = 0.0,
                                var longitude : Double = 0.0
) {

    companion object {
       fun getDangerCollection() : ArrayList<DangerCollectionData> {
           val data = ArrayList<DangerCollectionData>()
           val posters = intArrayOf(
               R.drawable.elg,
               R.drawable.ridende,
               R.drawable.kyr,
               R.drawable.raadyr

           )
           val titles = arrayOf(
               "Elg",
               "Ridende",
               "Kyr",
               "Rådyr"
           )
           titles.forEachIndexed { index, title ->
               val aDanger = DangerCollectionData(index, title, "$title is dangerous", posters[index], 59.12478, 11.38754)
               data.add(aDanger)
           }
           return data
       }
    }

}