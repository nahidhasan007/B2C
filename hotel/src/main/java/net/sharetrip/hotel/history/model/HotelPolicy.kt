package net.sharetrip.hotel.history.model

import com.squareup.moshi.Json

data class HotelPolicy(
    @Json(name = "paragraphs")
    var paragraphs: List<String>? = null,
    @Json(name = "title")
    var title: String? = null
)
