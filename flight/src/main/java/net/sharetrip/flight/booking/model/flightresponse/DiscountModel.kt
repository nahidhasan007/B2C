package net.sharetrip.flight.booking.model.flightresponse

import android.os.Parcelable
import kotlinx.parcelize.Parcelize

@Parcelize
data class DiscountModel(
    var type: String = "",
    var discountAmount: Double = 0.0,
    var coupon: String = "",
    var couponWithDiscount: Boolean = true,
    var gateway: List<String>? = null
) : Parcelable