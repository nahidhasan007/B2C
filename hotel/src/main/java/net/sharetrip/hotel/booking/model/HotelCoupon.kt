package net.sharetrip.hotel.booking.model

import net.sharetrip.hotel.booking.model.coupon.CouponPriceRange

data class HotelCoupon(
    val coupon: String,
    val title: String,
    val gatewayCodes: List<String>,
    var isSelected: Boolean = true,
    var properties: List<String>,
    var providers: List<String>,
    var rooms: List<String>,
    var serviceType: String,
    var priceRange: CouponPriceRange
)
