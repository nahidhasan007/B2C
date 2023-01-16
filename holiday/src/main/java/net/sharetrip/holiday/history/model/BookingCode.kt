package net.sharetrip.holiday.history.model

import com.squareup.moshi.Json

data class BookingCode(
    @Json(name = "bookingCode")
    var bookingCode: String = ""
)