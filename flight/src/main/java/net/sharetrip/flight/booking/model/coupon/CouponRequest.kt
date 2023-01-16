package net.sharetrip.flight.booking.model.coupon

open class CouponRequest(
        var coupon: String = "",
        var serviceType: String = "",
        var deviceType: String = ""
)