package net.sharetrip.flight.booking.model

import android.os.Parcelable
import com.squareup.moshi.Json
import kotlinx.android.parcel.Parcelize

@Parcelize
data class MealWheelchairResponse(
        @Json(name = "type")
        var type: String = "",
        @Json(name = "ssr")
        var ssr: List<Ssr>
) : Parcelable