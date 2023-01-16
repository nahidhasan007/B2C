package net.sharetrip.flight.booking.model

data class FlightCoupon(
    val airlineCodes: List<String>,
    val coupon: String,
    val gatewayCodes: List<String>,
    val title: String,
    val tripTypes: MutableList<String> = mutableListOf(),
    val airlineClasses: MutableList<String> = mutableListOf(),
    val expireDate: String = "",
    val serviceType: String = "",
    var isSelected: Boolean = true,
    var flightType: String,
    var priceRange: CouponPriceRange
)