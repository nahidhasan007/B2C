package net.sharetrip.visa.history.model

enum class VisaBookingStatus(val value: String) {
    PENDING("Pending"),
    CONFIRM("Confirm"),
    CANCELLED("Cancelled"),
    PROCESSING("Processing"),
    DECLINED("Declined")
}

