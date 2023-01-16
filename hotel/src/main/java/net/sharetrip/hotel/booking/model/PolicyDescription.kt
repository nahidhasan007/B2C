package net.sharetrip.hotel.booking.model

import com.squareup.moshi.Json

data class PolicyDescription(
    @Json(name = "paragraphs")
    var paragraphs: List<String> = ArrayList(),
    @Json(name = "title")
    var title: String? = null,
)
