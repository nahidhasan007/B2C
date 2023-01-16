package net.sharetrip.bus.booking.model

data class Coupon(
    val airlineCodes: List<String>,
    val coupon: String,
    val gatewayCodes: List<String>,
    val title: String,
    var isSelected: Boolean = true
)
