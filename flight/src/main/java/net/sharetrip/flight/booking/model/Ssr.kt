package net.sharetrip.flight.booking.model

import android.os.Parcelable
import com.squareup.moshi.Json
import kotlinx.android.parcel.Parcelize

@Parcelize
data class Ssr(
        @Json(name = "code")
        var code: String = "",
        @Json(name = "name")
        var name: String = ""
) : Parcelable