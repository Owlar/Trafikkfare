package no.hiof.oscarlr.trafikkfare.model

data class GasStation(
    var title: String,
    var vicinity: String,
    var rating: String,
    var latitude: Double,
    var longitude: Double
) {

    companion object {
        var gasStations = ArrayList<GasStation>()
    }
}