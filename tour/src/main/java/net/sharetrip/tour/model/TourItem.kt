package net.sharetrip.tour.model

import androidx.recyclerview.widget.DiffUtil

data class TourItem(
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
    val images: List<String>,
    val itinerary: String,
    val lowestPrice: Int,
    val notes: String,
    val periods: List<Period>,
    val pickupNotes: String,
    val points: Points,
    val productCode: String,
    val releaseTime: Int,
    val tax: String,
    val title: String
){
    companion object {

        var DIFF_CALLBACK: DiffUtil.ItemCallback<TourItem> =
            object : DiffUtil.ItemCallback<TourItem>() {
                override fun areItemsTheSame(oldItem: TourItem, newItem: TourItem): Boolean {
                    return oldItem.id == newItem.id
                }

                override fun areContentsTheSame(oldItem: TourItem, newItem: TourItem): Boolean {
                    return oldItem == newItem
                }
            }
    }
}
