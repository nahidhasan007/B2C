package net.sharetrip.hotel.booking.model

import android.os.Parcelable
import com.squareup.moshi.Json
import kotlinx.android.parcel.Parcelize

@Parcelize
data class Filter(
    @field:Json(name = "amenities")
    var amenityList: List<AmenityFilter>,
    @field:Json(name = "point_of_interest")
    var pointOfInterestList: List<PointOfInterest>,
    @field:Json(name = "neighborhood")
    var neighborhoodList: List<Neighborhood>,
    @field:Json(name = "meals")
    var mealList: List<Meals>,
    @field:Json(name = "propertyType")
    var propertyTypeList: List<PropertyType>,
    var priceRange: PriceRangeDumy,
    var locationRange: LocationRange,
    var searchHotel: SearchHotel,
    var ratingList: List<PropertyRating>
) : Parcelable
