package net.sharetrip.hotel.booking.model

import com.squareup.moshi.Json

data class RoomBookingResponse(
    @Json(name = "url")
    val url: String
)

