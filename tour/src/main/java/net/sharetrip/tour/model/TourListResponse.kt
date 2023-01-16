package net.sharetrip.tour.model

data class TourListResponse(
    val count: Int,
    val `data`: List<TourItem>,
    val filter: Filter,
    val limit: Int,
    val offset: Int
)
