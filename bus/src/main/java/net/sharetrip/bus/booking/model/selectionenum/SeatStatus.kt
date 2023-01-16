package net.sharetrip.bus.booking.model.selectionenum

enum class SeatStatus(val status: String) {
    AVAILABLE("Available"),
    BOOKED("Booked"),
    BLOCKED("Blocked"),
    SOLD("Sold"),
    SELECTED("Selected")
}
