package net.sharetrip.hotel.booking.model

import android.os.Parcelable
import kotlinx.android.parcel.Parcelize

@Parcelize
data class DiscountModel(
    var disCountMethod: String = "",
    var type: String = "",
    var discountAmount: Double = 0.0,
    var coupon: String = "",
    var couponWithDiscount: Boolean = false,
    var gateway: List<String>? = null,
) : Parcelable
