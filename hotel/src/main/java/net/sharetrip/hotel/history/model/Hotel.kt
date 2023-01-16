package net.sharetrip.hotel.history.model

import android.os.Parcelable
import com.squareup.moshi.Json
import kotlinx.parcelize.Parcelize

@Parcelize
data class Hotel(
    @Json(name = "amenities")
    var amenities: String,
    @Json(name = "images")
    var images: String,
    @Json(name = "thumbnail")
    var thumbnail: String,
    @Json(name = "address")
    var address: String,
    @Json(name = "kind")
    var kind: String,
    @Json(name = "center")
    var center: String,
    @Json(name = "postalCode")
    var postalCode: String?,
    @Json(name = "rating")
    var rating: Float,
    @Json(name = "policyStruct")
    var policyStruct: String,
    @Json(name = "amenityGroups")
    var amenityGroups: String,
    @Json(name = "createdAt")
    var createdAt: String,
    @Json(name = "cityName")
    var cityName: String,
    @Json(name = "descriptionStruct")
    var descriptionStruct: String,
    @Json(name = "phone")
    var phone: String,
    @Json(name = "name")
    var name: String,
    @Json(name = "indexId")
    var indexId: String,
    @Json(name = "countryName")
    var countryName: String,
    @Json(name = "starRating")
    var starRating: Float,
    @Json(name = "email")
    var email: String,
    @Json(name = "updatedAt")
    var updatedAt: String
) : Parcelable
