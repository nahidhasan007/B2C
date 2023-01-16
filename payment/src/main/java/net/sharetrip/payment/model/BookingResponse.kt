package net.sharetrip.payment.model

data class BookingResponse (
    val paymentUrl: String? = "",
    val successUrl: String? = "",
    val cancelUrl: String? = "",
    val declineUrl: String? = ""
)