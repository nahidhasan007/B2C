package net.sharetrip.hotel.booking.model

import com.squareup.moshi.Json

data class HotelDetailResponse(
    @Json(name = "id")
    var id: String? = null,
    @Json(name = "searchCode")
    var searchCode: String? = null,
    @Json(name = "hotel")
    var hotel: HotelDetail,
)
