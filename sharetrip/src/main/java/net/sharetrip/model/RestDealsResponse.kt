package net.sharetrip.model

data class RestDealsResponse(
    val status: String,
    val message: String,
    val response: DealsList
)
