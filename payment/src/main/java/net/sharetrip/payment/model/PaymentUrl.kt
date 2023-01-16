package net.sharetrip.payment.model

data class PaymentUrl(
    var service_type: String? = "",
    var status: String = "",
    var url: String = ""
)
