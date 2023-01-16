package net.sharetrip.hotel.booking.model

enum class PaymentStatus(val value: String) {
    PAID("PAID"),
    UNPAID("UNPAID"),
    CANCELLED("CANCELLED")
}
