package net.sharetrip.shared.utils

enum class HOTEL_BOOKING_STATUS(val value: String) {

    PROCESSING("PROCESSING"),
    WAITING("WAITING"),
    COMPLETED("COMPLETED"),
    UNSUCCESS("UNSUCCESS"),
    CANCELLED("CANCELLED")
}

enum class PAYMENT_STATUS(val value: String) {

    PAID("PAID"),
    UNPAID("UNPAID"),
    CANCELLED("CANCELLED")
}

enum class HOLIDAY_BOOKING_STATUS(val value: String) {

    PROCESSING("Processing"),
    WAITING("Waiting"),
    COMPLETED("Completed"),
    UNSUCCESS("Unsuccess"),
    CANCELLED("Cancelled")
}

enum class HOLIDAY_PAYMENT_STATUS(val value: String) {

    PAID("Paid"),
    UNPAID("UnPaid"),
    CANCELLED("Cancelled")
}

enum class FlightRevalidateStatus(val value: String) {

    SUCCESS("SUCCESS"),
    PRICE_CHANGE("PRICE_CHANGE"),
    ITINERARY_CHANGE("ITINERARY_CHANGE"),
    RE_ITINERARY_CHANGE ("RE-ITINERARY_CHANGE"),
    RE_VALIDATION_CHANGE("RE-VALIDATION_CHANGE")
}

enum class VISA_BOOKING_STATUS(val value: String) {
    PENDING("Pending"),
    CONFIRM("Confirm"),
    CANCELLED("Cancelled"),
    PROCESSING("Processing"),
    DECLINED("Declined")
}

enum class VISA_PAYMENT_STATUS(val value: String) {
    PAID("Paid"),
    UNPAID("UnPaid"),
    DECLINED("Declined"),
    CANCELLED("Cancelled")
}

enum class VISA_STATUS(val value: String) {
    PROCESSING("Processing"),
    APPROVED("Approved"),
    REJECTED("Rejected"),
    PENDING("Pending"),
    COMPLETED("Completed"),
    CANCELLED("Cancelled")
}