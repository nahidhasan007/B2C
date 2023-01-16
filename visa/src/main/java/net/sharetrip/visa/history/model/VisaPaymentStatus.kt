package net.sharetrip.visa.history.model

enum class VisaPaymentStatus(val value: String) {
    PAID("Paid"),
    UNPAID("UnPaid"),
    DECLINED("Declined"),
    CANCELLED("Cancelled")
}
