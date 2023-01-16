package net.sharetrip.hotel.booking.model

import com.squareup.moshi.Json

data class AmenityGroups(
    @field:Json(name = "groupName")
    var groupName: String = "",
    @field:Json(name = "amenities")
    var amenities: List<String> = ArrayList(),
)
