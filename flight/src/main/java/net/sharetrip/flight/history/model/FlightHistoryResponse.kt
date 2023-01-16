package net.sharetrip.flight.history.model

import android.os.Parcelable
import net.sharetrip.shared.utils.DateUtil
import com.squareup.moshi.Json
import kotlinx.android.parcel.Parcelize
import net.sharetrip.flight.booking.model.flightresponse.flights.price.PriceBreakdown

@Parcelize
data class FlightHistoryResponse(
    var providerCode: String? = null,
    var pnrCode: String? = null,
    var pnrMessage: String? = null,
    var eTicket: String = "",
    var airlineResCode: String? = null,
    var lastTicketingTime: String? = null,
    @field:Json(name = "lastCancellationTime")
    var lastCancellationTime: String? = null,
    var airFareRules: List<AirFareRule>? = null,
    var baggageInfo: List<BaggageInfo>? = null,
    var specialNote: String? = null,
    var bookingCode: String = "",
    var searchId: String = "",
    var sequenceNumber: String? = null,
    var sequenceCode: String = "",
    var gateWay: String? = null,
    var cardSeries: String? = null,
    @field:Json(name = "paymentStatus")
    var paymentStatus: String = "",
    @field:Json(name = "bookingStatus")
    var bookingStatus: String = "",
    @field:Json(name = "searchParams")
    var searchParams: SearchParams? = null,
    var searchParamDetails: List<SearchParamDetail>? = null,
    var totalAmount: Double = 0.0,
    var actualAmount: String? = null,
    var gatewayAmount: String? = null,
    var conversionRate: String? = null,
    var bookingCurrency: String? = null,
    var gatewayCurrency: String? = null,
    var discount: String? = null,
    var promotionalDiscount: String? = null,
    var discountType: String? = null,
    var couponCode: String? = null,
    var couponValue: String? = null,
    var status: Boolean = false,
    var promotionalDiscountApplied: String? = null,
    var price: String? = null,
    var perAdultPrice: String? = null,
    var perChildPrice: String? = null,
    var perInfantPrice: String? = null,
    @field:Json(name = "points")
    var point: Point? = null,
    var travellers: List<Traveller> = ArrayList(),
    var flight: List<Flight> = ArrayList(),
    var segments: List<Segment> = ArrayList(),
    var priceBreakdown: PriceBreakdown? = null,
    var covidAmount: Double = 0.0,
    var baggage: BaggageDetails? = null,
    var luggageAmount: Double = 0.0,
    val advanceIncomeTax: Double = 0.0,
    var convenienceFee: Double = 0.0,
    var VATOnConvenienceFee: Double = 0.0,
    val isModified: Boolean,
    val isVoidable: Boolean,
    val isRefundable: Boolean,
    val isReissueable: Boolean
) : Parcelable {

    val flightCode: String
        get() = when {
            flight.size == 1 -> {
                flight[0].originCode + " - " + flight[0].destinationCode
            }
            flight.size > 1 -> {
                val currentSize: Int = flight.size.minus(1)
                flight[0].originCode + " - " + flight[currentSize].originCode + " - " + flight[currentSize].destinationCode
            }
            else -> {
                ""
            }
        }

    val date: String
        get() = when {
            flight.size == 1 -> {
                val mBuilder = StringBuilder()
                mBuilder.append(DateUtil.parseDisplayDateFormatFromApiDateFormatData(flight[0].departureDateTime?.date))
                mBuilder.append(" - ")
                mBuilder.append(DateUtil.parseDisplayDateFormatFromApiDateFormatData(flight[0].arrivalDateTime?.date))
                    .toString()
            }
            flight.size > 1 -> {
                val currentSize: Int = flight.size.minus(1)
                val mBuilder = StringBuilder()
                mBuilder.append(DateUtil.parseDisplayDateFormatFromApiDateFormatData(flight[0].departureDateTime?.date))
                mBuilder.append(" - ")
                mBuilder.append(DateUtil.parseDisplayDateFormatFromApiDateFormatData(flight[currentSize].departureDateTime?.date))
                    .toString()
            }
            else -> {
                ""
            }
        }
}
