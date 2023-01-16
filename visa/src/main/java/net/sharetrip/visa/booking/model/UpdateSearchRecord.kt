package net.sharetrip.visa.booking.model

data class UpdateSearchRecord(
    val searchID: Int?,
    val productCode: String?,
    val visaType: String?,
    val price: Double?
)
