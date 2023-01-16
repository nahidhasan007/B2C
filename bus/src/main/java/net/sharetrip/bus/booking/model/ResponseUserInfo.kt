package net.sharetrip.bus.booking.model

data class ResponseUserInfo(
    val agent: Boolean,
    val companySlug: Any?,
    val email: String,
    val maxCreditLimit: Any?,
    val name: String,
    val phoneNumber: String,
    val userId: String
)
