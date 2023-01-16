package net.sharetrip.hotel.booking.model

data class SearchSummary(
    var hotelName: String = "",
    var location: String = "",
    var dateRange: String = "",
    var starRating: String = "",
    var kind: String = "",
    var rating: String = "",
    var rooms: Int = 0,
    var guest: Int = 0
)
