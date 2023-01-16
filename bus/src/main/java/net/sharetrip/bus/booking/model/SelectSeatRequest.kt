package net.sharetrip.bus.booking.model

data class SelectSeatRequest(
    val searchId: String,
    val coachId: String,
    val boardingPoint: Int?,
    val droppingPoint: Int?,
    val seats: List<String>
)
