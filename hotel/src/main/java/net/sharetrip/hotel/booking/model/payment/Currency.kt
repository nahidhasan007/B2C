package net.sharetrip.hotel.booking.model.payment

data class Currency(
    var code: String,
    val conversion: Conversion,
)
