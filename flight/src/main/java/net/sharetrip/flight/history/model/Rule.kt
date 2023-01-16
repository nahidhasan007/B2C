package net.sharetrip.flight.history.model

import android.os.Parcelable

import com.squareup.moshi.Json
import kotlinx.android.parcel.Parcelize

@Parcelize
data class Rule(
    @Json(name = "code") var code: String? = null,
    @Json(name = "type")
        var type: String? = null,
    @Json(name = "text")
        var text: String? = null
) : Parcelable

