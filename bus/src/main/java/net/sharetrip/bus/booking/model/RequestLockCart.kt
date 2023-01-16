package net.sharetrip.bus.booking.model

data class RequestLockCart(
    val coachId: String,
    val searchId: String,
    val passengers: Passengers
)
