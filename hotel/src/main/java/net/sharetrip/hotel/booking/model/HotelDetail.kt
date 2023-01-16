package net.sharetrip.hotel.booking.model

import com.squareup.moshi.Json

data class HotelDetail(
    @field:Json(name = "id")
    var id: String? = null,
    @field:Json(name = "name")
    var name: String,
    @field:Json(name = "starRating")
    var starRating: String,
    @field:Json(name = "lowestRate")
    var startingRate: Double = 0.0,
    @field:Json(name = "descriptionStruct")
    var overview: List<DescriptionOverview>? = null,
    @field:Json(name = "policyDescription")
    var policyDescription: List<PolicyDescription> = ArrayList(),
    @field:Json(name = "amenityGroups")
    var amenityGroups: List<AmenityGroups> = ArrayList(),
    @field:Json(name = "amenityLogo")
    var amenityLogo: List<Amenity> = ArrayList(),
    @field:Json(name = "images")
    var images: List<String> = ArrayList(),
    @field:Json(name = "thumbnail")
    var thumbnail: String? = null,
    @field:Json(name = "contact")
    var contact: List<Contact> = ArrayList(),
    @field:Json(name = "rating")
    var rating: String = "",
    @field:Json(name = "points")
    var points: Points,
    @field:Json(name = "kind")
    var kind: String = "",
    @field:Json(name = "discount")
    var discount: Int = 0,
    @field:Json(name = "lowestRateAfterDiscount")
    var lowestRateAfterDiscount: Double = 0.0,
)
