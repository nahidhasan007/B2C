package net.sharetrip.holiday.booking.model

import android.os.Parcelable

import com.squareup.moshi.Json
import kotlinx.android.parcel.Parcelize

@Parcelize
data class HolidayCity(
    var name: String,
    @field:Json(name = "cityCode")
    var code: String,
    var countryCode: String,
    var countryName: String,
    @field:Json(name = "image")
    var imageUrl: String
) : Parcelable
