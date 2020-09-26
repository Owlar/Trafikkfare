package no.hiof.oscarlr.trafikkfare.model

import no.hiof.oscarlr.trafikkfare.R

data class Danger(val uid : Int, var title : String, var description : String, var posterUrl : Int ) {

    companion object {
       fun getDangers() : ArrayList<Danger> {

           val data = ArrayList<Danger>()
           val poster = R.drawable.danger

           val titles = arrayOf(
               "Snøskred",
               "Sidevind",
               "Skiløpere"
           )

           titles.forEachIndexed { index, title ->
               val aDanger = Danger(index, title, title + " is dangerous", poster)
               data.add(aDanger)
           }

           return data
       }
    }

}