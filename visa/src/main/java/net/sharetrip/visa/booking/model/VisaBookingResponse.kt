package net.sharetrip.visa.booking.model

import com.squareup.moshi.Json

data class VisaBookingResponse(
    @Json(name = "url")
    val url: String
)
