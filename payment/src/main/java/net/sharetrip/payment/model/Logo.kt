package net.sharetrip.payment.model

import com.squareup.moshi.Json

data class Logo (
    @Json(name = "small")
    var small: String,
    @Json(name = "medium")
    var medium: String,
    @Json(name = "large")
    var large: String
)
