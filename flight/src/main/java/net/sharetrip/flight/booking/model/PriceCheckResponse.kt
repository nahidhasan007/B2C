package net.sharetrip.flight.booking.model

import com.squareup.moshi.Json
import net.sharetrip.flight.booking.model.flightresponse.flights.price.PriceBreakdown

data class PriceCheckResponse(
        @Json(name = "priceBreakdown")
        val priceBreakdown: PriceBreakdown? = null
)