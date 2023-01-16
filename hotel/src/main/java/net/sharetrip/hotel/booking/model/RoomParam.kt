package net.sharetrip.hotel.booking.model

import com.squareup.moshi.Json

class RoomParam {
    @field:Json(name = "id")
    var id: String? = null

    @field:Json(name = "guests")
    var guests: MutableList<List<GuestInfo>> = ArrayList()
}
