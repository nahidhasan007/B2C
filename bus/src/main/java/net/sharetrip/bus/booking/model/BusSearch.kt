package net.sharetrip.bus.booking.model

data class BusSearch(
    val journeyDate: String,
    val sourceStation: String,
    val destinationStation: String
)
