package net.sharetrip.flight.booking.model


import com.squareup.moshi.Json

data class Fare(
    @Json(name = "date")
    val date: String,
    @Json(name = "direct")
    val direct: Int,
    @Json(name = "nonDirect")
    val nonDirect: Int
)