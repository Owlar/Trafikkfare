package no.hiof.oscarlr.trafikkfare.model.jsonModel

data class StatensVegvesen(
    val candidates: List<Candidate>,
    val status: String
)