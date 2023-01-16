package net.sharetrip.flight.history.model

import android.os.Parcelable

import com.squareup.moshi.Json
import kotlinx.android.parcel.Parcelize

@Parcelize
data class BaggageInfo (
    @Json(name = "adult")
    var adult: String? = null,
    @Json(name = "child")
    var child: String? = null,
    @Json(name = "infant")
    var infant: String? = null,
    @Json(name = "type")
    var type: String? = null
    ):Parcelable
