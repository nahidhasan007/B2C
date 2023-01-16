package net.sharetrip.hotel.booking.model

enum class HotelBookingStatus(val value: String) {
    PROCESSING("PROCESSING"),
    WAITING("WAITING"),
    COMPLETED("COMPLETED"),
    UNSUCCESS("UNSUCCESS"),
    CANCELLED("CANCELLED")
}
