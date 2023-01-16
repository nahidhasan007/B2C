package net.sharetrip.hotel.booking.model

data class RoomBasicInfo(
    var roomId: String? = null,
    var perNightPrice: Double = 0.0,
    var perNightDiscountedPrice: Double = 0.0,
    var roomName: String = "",
    var gatewayType: String = "",
    var providerCode: String = "",
    var rooms: MutableList<Int> = ArrayList()
)
