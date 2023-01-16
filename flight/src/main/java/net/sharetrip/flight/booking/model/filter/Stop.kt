package net.sharetrip.flight.booking.model.filter

import com.squareup.moshi.Json

data class Stop(
        @Json(name = "id")
        var id: Int = 0,
        @Json(name = "name")
        var name: String? = null
)
