package net.sharetrip.flight.booking.model.flightresponse.flights.price

import android.os.Parcelable
import kotlinx.android.parcel.Parcelize

@Parcelize
data class PriceBreakdown(
    var subTotal: Double = 0.0,
    private var discount: Double = 0.0,
    var couponAmount: Double = 0.0,
    var discountAmount: Double = 0.0,
    var total: Double = 0.0,
    var originPrice: Double = 0.0,
    var promotionalAmount: Double = 0.0,
    var promotionalDiscount: Double = 0.0,
    var currency: String? = null,
    val details: List<PriceDetail>,
    var covidAmount: Double = 0.0,
    var luggageAmount: Double = 0.0,
    var redeemPoints: Double = 0.0,
    var advanceIncomeTax: Double = 0.0,
    var convenienceFee: Double = 0.0,
    var VATOnConvenienceFee: Double = 0.0
) : Parcelable {

    fun getDiscount(): Double {
        return originPrice - promotionalAmount
    }

    fun calculateForHistory(): Double {
        return originPrice - discountAmount - couponAmount + advanceIncomeTax + luggageAmount + covidAmount
    }
}
