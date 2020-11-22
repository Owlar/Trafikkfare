package no.hiof.oscarlr.trafikkfare.model.gson

data class Candidate(
    val formatted_address: String,
    val geometry: Geometry,
    val name: String,
    val opening_hours: OpeningHours
)