package net.sharetrip.hotel.booking.model

import android.os.Parcelable
import com.squareup.moshi.Json
import kotlinx.android.parcel.Parcelize

@Parcelize
data class CancellationPolicy(
    @field:Json(name = "nonRefundable")
    var isNonRefundable: Boolean = false,
    @field:Json(name = "freeCancellationDate")
    var freeCancellationDate: String? = null,
    @field:Json(name = "fetched")
    var isFetched: Boolean = false
) : Parcelable
