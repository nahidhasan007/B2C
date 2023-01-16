package net.sharetrip.bus.booking.model

class CoachList(
    val coaches: List<Departure>,
    val discount: Double,
    val discountType: Int,
    val fromStationName: String,
    val journeyDate: String,
    val markup: Int,
    val markupType: Int,
    val numberOfBuses: Int,
    val searchId: String,
    val toStationName: String,
    val tripCoin: Int?
)
