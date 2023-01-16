package net.sharetrip.hotel.booking.model

import com.squareup.moshi.Json

data class HotelBookingParams(
    @field:Json(name = "roomSearchCode")
    var searchCode: String = "",
    @field:Json(name = "gatewayId")
    var gatewayId: String = "",
    @field:Json(name = "cardSeries")
    var cardSeries: String = "",
    @field:Json(name = "tripCoin")
    var tripCoin: String = "",
    @field:Json(name = "rooms")
    var rooms: MutableList<RoomParam> = ArrayList(),
    @field:Json(name = "primaryContact")
    var primaryContact: PrimaryContact? = null,
    @field:Json(name = "coupon")
    var coupon: String? = null,
    var verifiedMobileNumber: String? = "",
    var otp: String? = ""
)
