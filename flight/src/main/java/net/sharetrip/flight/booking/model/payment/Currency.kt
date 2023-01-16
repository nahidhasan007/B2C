package net.sharetrip.flight.booking.model.payment

data class Currency(
        var code: String,
        val conversion: Conversion
)