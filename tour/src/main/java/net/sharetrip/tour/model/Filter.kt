package net.sharetrip.tour.model

data class Filter(
    val durations: List<Int>,
    val priceRange: PriceRange
)
