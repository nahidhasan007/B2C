package net.sharetrip.tracker.model

import android.os.Parcelable
import com.squareup.moshi.Json
import kotlinx.android.parcel.Parcelize

@Parcelize
data class FlightPosition(
        @field:Json(name = "lon")
        var longitude: Double,
        @field:Json(name = "lat")
        var latitude: Double,
        val speedMph: Double,
        val altitudeFt: Double,
        var airLineDetails: String = ""
) : Parcelable
