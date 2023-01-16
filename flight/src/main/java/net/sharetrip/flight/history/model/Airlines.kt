package net.sharetrip.flight.history.model

import android.os.Parcelable

import com.squareup.moshi.Json
import kotlinx.android.parcel.Parcelize

@Parcelize
data class Airlines(
        @Json(name = "full")
        var full: String? = null,
        @field:Json(name = "short")
        var shortName: String? = null,
        @Json(name = "code")
        var code: String? = null
) : Parcelable
