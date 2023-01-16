package net.sharetrip.flight.history.model


import android.os.Parcelable
import com.squareup.moshi.Json
import kotlinx.android.parcel.Parcelize

@Parcelize
data class Detail (
    @Json(name = "type")
    var type: String? = null,
    @Json(name = "baseFare")
    var baseFare: String? = null,
    @Json(name = "tax")
    var tax: String? = null,
    @Json(name = "total")
    var total: String? = null,
    @Json(name = "currency")
    var currency: String? = null,
    @Json(name = "numberPaxes")
    var numberPaxes: String? = null
): Parcelable

