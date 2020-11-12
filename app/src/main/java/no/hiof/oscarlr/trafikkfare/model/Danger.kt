package no.hiof.oscarlr.trafikkfare.model

class Danger(val uid : String = "",
             var title : String = "",
             var description : String = "",
             var posterUrl : Int = 0,
             var latitude : Double = 0.0,
             var longitude : Double = 0.0) {

    companion object {
        private var dangers = ArrayList<Danger>()

        fun setFromFirestore(dangersFromFirestore: ArrayList<Danger>) {
            dangers = dangersFromFirestore
        }

        fun getDangers() : ArrayList<Danger> {
            return dangers
        }

        fun setDangers(dangerList: ArrayList<Danger>) {
            dangerList.forEach {
                if (!this.dangers.contains(it))
                    this.dangers.add(it)
            }
        }
    }

}