package net.sharetrip.hotel.booking.model

class RoomNo(val roomNo: Int) {
    val description: String
        get() {
            return "Guests of Room $roomNo"
        }
}