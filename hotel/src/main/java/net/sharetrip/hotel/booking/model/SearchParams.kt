package net.sharetrip.hotel.booking.model

import com.squareup.moshi.Json

data class SearchParams(
    @Json(name = "checkin")
    var checkin: String,
    @Json(name = "checkout")
    var checkout: String,
    @Json(name = "currency")
    var currency: String,
    @Json(name = "totalNights")
    var totalNights: Int = 0,
    @Json(name = "rooms")
    var rooms: String,
    var propertyCode: String
)
