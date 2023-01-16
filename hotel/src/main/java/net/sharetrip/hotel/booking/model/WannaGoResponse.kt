package net.sharetrip.hotel.booking.model

import com.squareup.moshi.Json

data class WannaGoResponse(
    @Json(name = "cities")
    var cities: List<ReviewCity> = ArrayList(),
    @Json(name = "perWinEarn")
    var perWinEarn: Int = 0
)
