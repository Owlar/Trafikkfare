package no.hiof.oscarlr.trafikkfare.model

class ClosestStatensVegvesen(val name : String = "",
                             val address : String = "",
                             val isOpen : Boolean = false,
                             val latitude : Double = 0.0,
                             val longitude : Double = 0.0

) {
    companion object {
        var closestStatensVegvesen : ClosestStatensVegvesen = ClosestStatensVegvesen()
    }
}
