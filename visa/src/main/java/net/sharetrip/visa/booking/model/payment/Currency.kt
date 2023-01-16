package net.sharetrip.visa.booking.model.payment

import net.sharetrip.visa.booking.model.Conversion

data class Currency(
    var code: String,
    val conversion: Conversion,
)
