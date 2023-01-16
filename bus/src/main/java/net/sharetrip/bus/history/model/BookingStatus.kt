package net.sharetrip.bus.history.model

enum class BookingStatus(val value:Int) {
    FAILED(0),
    BOOKED(1),
    CONFIRMED(2),
    CANCELED(3)
}
