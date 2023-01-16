package net.sharetrip.tour.model

import com.squareup.moshi.Json

class TourHistoryResponse {

    @Json(name = "data")
    var data: List<TourHistoryItem>? = null

    @Json(name = "offset")
    var offset: Int = 0

    @Json(name = "count")
    var count: Int = 0

    @Json(name = "limit")
    var limit: Int = 0
}
