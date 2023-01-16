package net.sharetrip.holiday.booking.model

import android.os.Parcelable

import com.squareup.moshi.Json
import kotlinx.android.parcel.Parcelize

@Parcelize
data class HolidayOffer(
    val id: Int,
    var infant: Int = 0,
    var infantDiscountPrice: Int = 0,
    var child3To6: Int = 0,
    var child3To6DiscountPrice: Int = 0,
    var child7To12: Int = 0,
    var child7To12DiscountPrice: Int = 0,
    var singlePerPax: Int = 0,
    var singlePerPaxDiscountPrice: Int = 0,
    var doublePerPax: Int = 0,
    var doublePerPaxDiscountPrice: Int = 0,
    var twinPerPax: Int = 0,
    var twinPerPaxDiscountPrice: Int = 0,
    var triplePerPax: Int = 0,
    var triplePerPaxDiscountPrice: Int = 0,
    var quadPerPax: Int = 0,
    var quadPerPaxDiscountPrice: Int = 0,
    var categoryName: String? = null,
    var periodTo: String? = null,
    var periodFrom: String? = null,
    var currency: String? = null,
    var category: String? = null,
    var departs: String? = null,
    val specificDays: String?,
    var departureTime: String? = null,
    @field:Json(name = "package_periods_hotels")
    var hotels: List<Hotel>? = null
) : Parcelable
