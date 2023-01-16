package net.sharetrip.model

data class DealsList(
    val data: List<Deal>,
    val offset: Int,
    val limit: Int
)
