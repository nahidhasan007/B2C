package net.sharetrip.shared.model

import android.os.Parcelable
import com.squareup.moshi.Json
import kotlinx.parcelize.Parcelize

@Parcelize
data class AdvanceSearchResponse(
    @Json(name = "fare")
    val fare: List<Fare>,
    @Json(name = "max")
    val max: Max,
    @Json(name = "min")
    val min: Min
) : Parcelable