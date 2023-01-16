package net.sharetrip.visa.booking.model

data class VisaListResponse(
    val data: List<VisaItem>? = null,
    val offset: Int? = null,
    val count: Int? = null,
    val limit: Int? = null
)
