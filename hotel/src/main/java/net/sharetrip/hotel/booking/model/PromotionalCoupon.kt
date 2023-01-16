package net.sharetrip.hotel.booking.model

import android.os.Parcelable
import kotlinx.parcelize.Parcelize

@Parcelize
data class PromotionalCoupon(
    val coupon: String,
    val discount: Int,
    val discountType: String,
    val minimumTotalAmount: Int,
    val rooms: List<Int>,
    val title: String,
    val withDiscount: String,
    var isSelected: Boolean = true
) : Parcelable