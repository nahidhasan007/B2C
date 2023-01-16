package net.sharetrip.hotel.booking.model.coupon

data class HotelExtraParams(
    val searchId: String,
    val providerCode: String,
    val propertyCode: String,
    val rooms: MutableList<Int>,
    val roomSearchCode: String,
    val propertyRoomId: String
)
