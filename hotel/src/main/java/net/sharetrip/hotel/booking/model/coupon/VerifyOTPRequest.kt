package net.sharetrip.hotel.booking.model.coupon

data class VerifyOTPRequest(
    val mobileNumber: String,
    val otp: String? = null
)