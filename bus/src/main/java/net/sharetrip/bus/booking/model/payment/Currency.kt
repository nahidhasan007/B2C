package net.sharetrip.bus.booking.model.payment

data class Currency(
    var code: String,
    val conversion: Conversion,
)
