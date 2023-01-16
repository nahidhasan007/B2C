package net.sharetrip.bus.booking.model

data class Trip(
    val boardingPoint: BoardingPoints,
    val coachNo: String,
    val coachSeatList: List<Seat>?,
    val coachType: String,
    val company: String,
    val droppingPoint: BoardingPoints,
    val journeyDate: String,
    val route: Route,
    val serviceCharge: Double
)
