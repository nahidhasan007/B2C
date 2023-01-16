package net.sharetrip.tour.model

data class TourDetailResponseNew(
    val cancellationPolicy: String,
    val cityName: String,
    val countryCode: String,
    val countryName: String,
    val currency: String,
    val cxlPolicy: Int,
    val duration: String,
    val durationType: String,
    val generalCondition: String,
    val id: Int,
    val images: List<Image>,
    val itinerary: String,
    val lowestPrice: Int,
    val notes: String,
    val periods: List<PeriodX>,
    val pickupNotes: String,
    val points: Points,
    val productCode: String,
    val releaseTime: Int,
    val tax: String,
    val title: String
)
