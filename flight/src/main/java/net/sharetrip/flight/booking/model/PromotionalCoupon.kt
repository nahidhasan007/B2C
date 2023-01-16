package net.sharetrip.flight.booking.model

import android.os.Parcelable
import kotlinx.android.parcel.Parcelize

@Parcelize
data class PromotionalCoupon(
    val coupon: String = "",
    val discount: Int = -1,
    val discountType: String = "",
    val minimumTotalAmount: Int = -1,
    val title: String = "",
    val withDiscount: String = "",
    var isSelected: Boolean = false
) : Parcelable
