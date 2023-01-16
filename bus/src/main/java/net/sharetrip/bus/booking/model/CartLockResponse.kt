package net.sharetrip.bus.booking.model

data class CartLockResponse(
    val id: String,
    val passengerInfos: List<Passengers>,
    val tripInfos: Trip
)
