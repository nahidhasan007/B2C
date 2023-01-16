package net.sharetrip.holiday.booking.model

import com.squareup.moshi.Json

data class HolidayListResponse(
    @field:Json(name = "data")
    var data: List<HolidayItem> = ArrayList(),
    @Json(name = "count")
    var count: Int = 0,
    @Json(name = "limit")
    var limit: Int = 0,
    @Json(name = "offset")
    var offset: Int = 0
)
