package net.sharetrip.flight.booking.model.coupon

data class GpLoyaltyCheckRequest(
    val mobileNumber: String
)

data class VerifyOTPRequest(
    val mobileNumber: String,
    val otp: String? = null
)