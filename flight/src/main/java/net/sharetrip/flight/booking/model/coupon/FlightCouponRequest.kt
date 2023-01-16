package net.sharetrip.flight.booking.model.coupon

data class FlightCouponRequest(var extraParams: FlightExtraParams? = null) : CouponRequest()