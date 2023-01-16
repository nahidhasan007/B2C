package net.sharetrip.visa.booking.model

data class VisaFaq(
    val question: String? = null,
    val answer: String? = null,
    var isExpanded: Boolean = false
)
