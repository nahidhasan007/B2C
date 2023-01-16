package net.sharetrip.flight.booking.model.coupon

data class GPLoyaltyCheckResponse(
    val loyaltyStatus: String? = "No",
    val success: Boolean? = false,
    val otpExpirationInMin :String
)
