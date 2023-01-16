package net.sharetrip.holiday.booking.model

import android.text.SpannableString
import android.text.Spanned
import android.text.style.StrikethroughSpan
import android.view.View.GONE
import android.view.View.VISIBLE
import com.squareup.moshi.Json
import java.text.NumberFormat
import java.util.*

data class HolidayDetailResponse(
    var featured: String = "YES",
    var notes: String? = null,
    var arrivalTransportName: String? = null,
    var arrivalTransportCode: String? = null,
    var arrivalTime: String? = null,
    var generalCondition: String? = null,
    var itinerary: String? = null,
    var departureTransportName: String? = null,
    var departureTransportCode: String? = null,
    var departureTime: String = "00:00",
    var departurePickupTime: String? = null,
    var id: Int = 0,
    var images: List<ImagesItem>? = null,
    var tax: String? = null,
    var featuredText: String? = null,
    val countryName: String,
    var pickupNotes: String? = null,
    var pickupPoint: String? = null,
    var lowestPrice: Int = 0,
    @field:Json(name = "points")
    var point: Point? = null,
    var releaseTime: Int = 0,
    val cityCode: String,
    var excludedService: String? = null,
    var includedService: String? = null,
    val title: String,
    var duration: Int = 0,
    val searchID: Int = 0,
    var withAirfare: String? = null,
    var cityName: String? = null,
    var countryCode: String? = null,
    var cancellationPolicy: String? = null,
    var locations: List<String> = ArrayList(),
    var cxlPolicy: String? = null,
    var departs: String? = null,
    var productCode: String? = null,
    var highlights: String? = null,
    var pickupNote: String? = null,
    var discountPrice: Int = 0,
    var discount: Int = 0,
    var discountType: String = "",
    var gatewayCurrency: String,

    @field:Json(name = "periods")
    var offers: List<HolidayOffer> = ArrayList(),

    @field:Json(name = "currency")
    val currencyCode: String,

    @field:Json(name = "bankGateway")
    val bankGatewayList: List<String> = ArrayList()
) {
    val formattedRealPrice: SpannableString
        get() {
            if (discount > 0) {
                val price = NumberFormat.getNumberInstance(Locale.US).format(lowestPrice)
                val string = SpannableString("$currencyCode $price")
                string.setSpan(
                    StrikethroughSpan(),
                    0,
                    string.length,
                    Spanned.SPAN_EXCLUSIVE_EXCLUSIVE
                )
                return string
            }
            return SpannableString("")
        }

    val discountAmount: String
        get() {
            if (discount > 0) {
                if (discountType.contentEquals(HolidayDiscountType.Percentage.toString())) {
                    return NumberFormat.getNumberInstance(Locale.US).format(discount) + "% OFF"
                }
            }
            return ""
        }

    val formattedDiscountPrice: String
        get() {
            return currencyCode + " " + NumberFormat.getNumberInstance(Locale.US)
                .format(discountPrice)
        }

    val discountVisibility: Int
        get() {
            return if (discount > 0) VISIBLE else GONE
        }
}