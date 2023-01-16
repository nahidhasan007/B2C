package net.sharetrip.flight.history.model

import android.os.Parcelable

import com.squareup.moshi.Json
import kotlinx.android.parcel.Parcelize

@Parcelize
data class SearchParamDetail (
    @Json(name = "sequence")
    var sequence: Int? = null,
    @Json(name = "origin")
    var origin: String? = null,
    @Json(name = "destination")
    var destination: String? = null,
    @Json(name = "departureDateTime")
    var departureDateTime: String? = null
):  Parcelable