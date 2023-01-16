package net.sharetrip.tracker.view.cirium.model

data class StatusDetails (
    val airPathCode: String,
    val airlineName: String,
    val airlineNumber: String,
    val departureDate: String,
    val departureTerminal: String,
    val departureGate: String,
    val departureAirport: String,
    val departureDelay: String,
    val arrivalDate: String,
    val arrivalTerminal: String,
    val arrivalGate: String,
    val arrivalAirport: String,
    val arrivalDelay: String,
    val status: String
)
