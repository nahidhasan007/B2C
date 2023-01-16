package net.sharetrip.hotel.booking.model

import com.squareup.moshi.Json

data class DescriptionOverview(
    @Json(name = "paragraphs")
    var paragraphs: List<String>? = null,
    @Json(name = "title")
    var title: String? = null,
)
