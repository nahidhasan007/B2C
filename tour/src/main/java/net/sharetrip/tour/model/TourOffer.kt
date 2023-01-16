package net.sharetrip.tour.model

import android.os.Parcelable

import com.squareup.moshi.Json
import kotlinx.android.parcel.Parcelize

@Parcelize
data class TourOffer(

    @Json(name = "child3To6")
    var child3To6: Int = 0,

    @Json(name = "departureTime")
    var departureTime: String? = null,

    @Json(name = "price3Pax")
    var price3Pax: Int = 0,

    /*@Json(name = "note")
    var note: Any? = null,*/

    @Json(name = "periodTo")
    var periodTo: String? = null,

    @Json(name = "perPersonPax")
    var perPersonPax: Int = 0,

    @Json(name = "price5Pax")
    var price5Pax: Int = 0,

    @Json(name = "infant0To2")
    var infant0To2: Int = 0,

    @Json(name = "price7Pax")
    var price7Pax: Int = 0,

    @Json(name = "price9Pax")
    var price9Pax: Int = 0,

    @Json(name = "rating")
    var rating: Int = 0,

    @Json(name = "specificDays")
    var specificDays: String? = null,

    @Json(name = "max10Pax")
    var max10Pax: Int = 0,

    @Json(name = "child7To12")
    var child7To12: Int = 0,

    @Json(name = "id")
    var id: Int = 0,

    @Json(name = "serviceBasis")
    var serviceBasis: String? = null,

    @Json(name = "departs")
    var departs: String? = null,

    @Json(name = "price4Pax")
    var price4Pax: Int = 0,

    @Json(name = "price2Pax")
    var price2Pax: Int = 0,

    @Json(name = "price8Pax")
    var price8Pax: Int = 0,

    @Json(name = "price6Pax")
    var price6Pax: Int = 0,

    @Json(name = "multiTime")
    var multiTime: String? = null,

    @Json(name = "periodFrom")
    var periodFrom: String? = null,

    @Json(name = "dailyTourId")
    var dailyTourId: Int = 0,

    @Json(name = "price10Pax")
    var price10Pax: Int = 0,

    @Json(name = "status")
    var status: String? = null
) : Parcelable
