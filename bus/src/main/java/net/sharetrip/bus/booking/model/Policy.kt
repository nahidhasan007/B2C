package net.sharetrip.bus.booking.model

import android.os.Parcelable
import com.squareup.moshi.Json
import kotlinx.android.parcel.Parcelize

@Parcelize
data class Policy(
    @Json(name = "header")
    var header: List<Header>? = null,
    @Json(name = "rules")
    var rules: List<Rule>? = null
) : Parcelable
