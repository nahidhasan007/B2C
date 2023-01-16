package net.sharetrip.tour.model

import com.squareup.moshi.Json

data class TourBookingResponse(
    @Json(name = "url")
    var url: String? = null
)
