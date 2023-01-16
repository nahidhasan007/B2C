package net.sharetrip.flight.booking.model.coupon

data class HotelCouponRequest(var extraParams: HotelExtraParams? = null) : CouponRequest()