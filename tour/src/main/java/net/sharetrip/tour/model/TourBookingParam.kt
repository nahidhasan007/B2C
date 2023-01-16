package net.sharetrip.tour.model

import android.os.Parcelable
import com.squareup.moshi.Json
import kotlinx.android.parcel.Parcelize

@Parcelize
data class TourBookingParam(

    @Json(name = "tourId")
    var tourId: Int = 0,

    @Json(name = "tourPeriodId")
    var tourPeriodId: Int = 0,

    @Json(name = "tourDate")
    var tourDate: String? = null,

    @Json(name = "departureTime")
    var departureTime: String? = null,

    @Json(name = "coupon")
    var coupon: String? = "",

    @Json(name = "totalAmount")
    var totalAmount: Int = 0,

    @Json(name = "bookingCurrency")
    var bookingCurrency: String? = null,

    @Json(name = "departurePickUpLocationName")
    var departurePickUpLocationName: String? = null,

    @Json(name = "countryCode")
    var countryCode: String? = null,

    @Json(name = "adultsCount")
    var adultsCount: Int = 0,

    @Json(name = "infantCount")
    var infantCount: Int = 0,

    @Json(name = "child3To6Count")
    var child3To6Count: Int = 0,

    @Json(name = "child7To12Count")
    var child7To12Count: Int = 0,

    @Json(name = "gateWay")
    var gateway: String? = null,

    @Json(name = "primaryContact")
    var primaryContact: PrimaryContact? = null
) : Parcelable
