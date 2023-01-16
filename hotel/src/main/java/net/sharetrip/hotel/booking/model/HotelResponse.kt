package net.sharetrip.hotel.booking.model

import com.squareup.moshi.Json

data class HotelResponse(
    @Json(name = "searchCode")
    var searchCode: String,
    @field:Json(name = "soffset")
    var offset: Int = 0,
    @Json(name = "limit")
    var limit: Int = 0,
    @Json(name = "currency")
    var currency: String,
    @Json(name = "totalHotels")
    var totalHotels: Int = 0,
    @Json(name = "priceRange")
    var priceRange: PriceRange,
    @field:Json(name = "filters")
    var filter: Filter,
    @field:Json(name = "hotels")
    var hotelList: MutableList<HotelInfo> = ArrayList()
)
