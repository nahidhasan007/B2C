package net.sharetrip.hotel.history.model

import com.squareup.moshi.Json

data class HotelHistoryListResponse(
    @field:Json(name = "list")
    var hotelHistoryItems: MutableList<HotelHistoryItem> = ArrayList(),
    @Json(name = "limit")
    var limit: Int = 0,
    @Json(name = "offset")
    var offset: Int = 0,
    @Json(name = "count")
    var count: Int = 0
)
