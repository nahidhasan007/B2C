package net.sharetrip.flight.booking.model.filter

import android.os.Parcelable
import com.squareup.moshi.Json
import kotlinx.parcelize.Parcelize
import net.sharetrip.flight.booking.model.Price

@Parcelize
data class FilterParams(
    @Json(name = "price")
        var price: Price? = null,
    @Json(name = "isRefundable")
    var isRefundable: List<Int> ? = null,
    @Json(name = "refundable")
    var refundable: Int? = null,    //todo on api stability
    @Json(name = "outbound")
        var outbound: String? = null,
    @Json(name = "airlines")
        var airlines: List<String>? = null,
    @Json(name = "weight")
        var weight: List<String>? = null,
    @Json(name = "stops")
        var stops: List<String>? = null,
    @Json(name = "layover")
        var layover: List<String>? = null,
    @Json(name = "return")
        var returnTime: String? = null,
    @Json(name = "sort")
        var sort: String? = null
) : Parcelable
