package no.hiof.oscarlr.trafikkfare.model

import no.hiof.oscarlr.trafikkfare.R

data class Danger(val uid : Int, var title : String, var description : String, var posterUrl : Int ) {

    companion object {
       fun getDangers() : ArrayList<Danger> {
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
               val aDanger = Danger(index, title, "$title is dangerous", posters[index])
               data.add(aDanger)
           }
           return data
       }
    }

}