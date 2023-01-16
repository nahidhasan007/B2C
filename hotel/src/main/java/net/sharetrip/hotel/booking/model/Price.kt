package net.sharetrip.hotel.booking.model

import android.os.Parcelable
import com.squareup.moshi.Json
import kotlinx.android.parcel.Parcelize

@Parcelize
data class Price(
    @field:Json(name = "perNight")
    var perNight: Double = 0.0,
    @field:Json(name = "total")
    var total: Double = 0.0,
    @field:Json(name = "discount")
    var discount: Int = 0,
    @field:Json(name = "perNightPriceAfterDiscount")
    var discountedPerNight: Double = 0.0,
    @field:Json(name = "totalPriceAfterDiscount")
    var discountedTotal: Double = 0.0
) : Parcelable
