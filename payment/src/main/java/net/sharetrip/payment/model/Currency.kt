package net.sharetrip.payment.model

data class Currency(
    var code: String,
    val conversion: Conversion
)