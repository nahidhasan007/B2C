package net.sharetrip.flight.booking.model

import android.os.Parcelable
import com.squareup.moshi.Json
import kotlinx.android.parcel.Parcelize

@Parcelize
data class Price(
        @Json(name = "max")
        var max: Float,
        @Json(name = "min")
        var min: Float
) : Parcelable
