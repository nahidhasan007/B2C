package net.sharetrip.hotel.booking.model.coupon

data class GPLoyaltyCheckResponse(
    val loyaltyStatus: String? = "No",
    val success: Boolean? = false,
    val otpExpirationInMin :String
)
