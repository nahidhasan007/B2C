package net.sharetrip.tracker.view.cirium.model

data class FlightLiveTrackInfo (
    val flightId : Int,
    val carrierFsCode : String,
    val flightNumber : Int,
    val tailNumber : String,
    val departureAirportFsCode : String,
    val arrivalAirportFsCode : String,
    val equipment : String,
    val bearing : Double,
    val heading : Double,
    val positions : List<FlightPosition>?
)
