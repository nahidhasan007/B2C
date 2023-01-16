package net.sharetrip.hotel.booking.model

import android.os.Parcelable
import kotlinx.android.parcel.Parcelize

@Parcelize
data class HotelFilterData(
    var priceRange: PriceRange? = null,
    var propertyRating: ArrayList<String> = ArrayList(),
    var propertyType: ArrayList<String> = ArrayList(),
    var meals: ArrayList<String> = ArrayList(),
    var amenities: ArrayList<String> = ArrayList(),
    var neighborhoods: ArrayList<String> = ArrayList(),
    var pointOfInterests: ArrayList<String> = ArrayList(),
    var hotelName: String = "",
    var distance: String = ""
) : Parcelable
