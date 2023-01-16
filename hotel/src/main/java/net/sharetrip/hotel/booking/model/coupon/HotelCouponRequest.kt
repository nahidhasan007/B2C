package net.sharetrip.hotel.booking.model.coupon

data class HotelCouponRequest(var extraParams: HotelExtraParams? = null) : CouponRequest()