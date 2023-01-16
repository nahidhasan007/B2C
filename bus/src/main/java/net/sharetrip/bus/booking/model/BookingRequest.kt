package net.sharetrip.bus.booking.model

data class BookingRequest(
    val searchId: String,
    val cartId: String,
    val gateWay: String,
    val cardSeries: String?,
    val coupon: String?,
    val tripCoin: Int?,
)
