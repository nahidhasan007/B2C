package net.sharetrip.flight.booking.model


import com.squareup.moshi.Json

data class Max(
    @Json(name = "direct")
    val direct: Double,
    @Json(name = "nonDirect")
    val nonDirect: Double
)