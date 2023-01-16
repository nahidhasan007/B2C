package net.sharetrip.tracker.view.cirium.model

data class StatusInfo (
        val airlineName: String,
        val flightNumber: String,
        val airlineIcon: String,
        val departureTime: String,
        val departureStationCode: String,
        val arrivalTime: String,
        val arrivalStationCode: String,
        val status: String
)
