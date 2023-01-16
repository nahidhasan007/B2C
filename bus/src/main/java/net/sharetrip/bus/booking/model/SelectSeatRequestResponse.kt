package net.sharetrip.bus.booking.model

data class SelectSeatRequestResponse(
    val id: String,
    val tripInfos: Trip?
)
