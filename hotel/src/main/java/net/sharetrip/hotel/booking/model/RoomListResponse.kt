package net.sharetrip.hotel.booking.model

import com.squareup.moshi.Json

data class RoomListResponse(
    @field:Json(name = "rooms")
    var rooms: List<List<Rooms>>,
    @Json(name = "searchParams")
    var searchParams: SearchParams,
    @field:Json(name = "roomSearchCode")
    var roomSearchCode: String,
    var promotionalCoupon: List<PromotionalCoupon>
)
