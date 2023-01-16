package net.sharetrip.flight.booking.model.flightresponse.flights

import android.os.Parcelable
import com.squareup.moshi.Json
import kotlinx.android.parcel.Parcelize
import net.sharetrip.flight.booking.model.PromotionalCoupon
import net.sharetrip.flight.booking.model.flightresponse.flights.flight.Flight
import net.sharetrip.flight.booking.model.flightresponse.flights.price.PriceBreakdown
import net.sharetrip.flight.booking.model.flightresponse.flights.segment.Segments

@Parcelize
data class Flights(
    val attachment: Boolean,
    @Json(name = "sequence")
    var sequence: String,
    @Json(name = "deal")
    var deal: String = "",
    @Json(name = "shareLink")
    var shareLink: String? = null,
    @Json(name = "seatsLeft")
    var seatsLeft: Int = 0,
    @Json(name = "refundable")
    var refundable: Boolean = false,
    @Json(name = "isRefundable")
    var isRefundable: String? = "",
    @Json(name = "weight")
    var weight: String? = null,
    @Json(name = "price")
    var price: Int = 0,
    @Json(name = "discount")
    var discount: Double = 0.0,
    @Json(name = "originPrice")
    var originPrice: String? = null,
    @Json(name = "earnPoint")
    var earnPoint: Int = 0,
    @Json(name = "sharePoint")
    var sharePoint: Int = 0,
    @Json(name = "currency")
    var currency: String? = null,
    @Json(name = "perAdultPrice")
    var perAdultPrice: String? = null,
    @Json(name = "perChildPrice")
    var perChildPrice: String? = null,
    @Json(name = "perInfantPrice")
    var perInfantPrice: String? = null,
    @Json(name = "domestic")
    var domestic: Boolean,
    @Json(name = "flight")
    var flight: List<Flight>,
    @Json(name = "segments")
    var segments: List<Segments>,
    @Json(name = "priceBreakdown")
    var priceBreakdown: PriceBreakdown,
    var gatewayCurrency: String,
    var availableCoupons: List<PromotionalCoupon>?,
    var baggageWeightText: String
) : Parcelable {
    var refundableText: String
        get() {
            return if (!isRefundable.isNullOrEmpty()) isRefundable.toString()
            else ""
        }
        set(value) {}


    fun setBaggageWeightText() {
        baggageWeightText = "${weight?.filter { it.isDigit() }} ${weight?.filter { it.isLetter() }}"
    }
}
