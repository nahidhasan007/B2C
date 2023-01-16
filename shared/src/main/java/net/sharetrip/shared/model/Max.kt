package net.sharetrip.shared.model

import android.os.Parcelable
import com.squareup.moshi.Json
import kotlinx.parcelize.Parcelize

@Parcelize
data class Max(
    @Json(name = "direct")
    val direct: Double,
    @Json(name = "nonDirect")
    val nonDirect: Double
) : Parcelable