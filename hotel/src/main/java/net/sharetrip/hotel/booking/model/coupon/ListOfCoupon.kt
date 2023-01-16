package net.sharetrip.hotel.booking.model.coupon

import android.os.Parcelable
import kotlinx.parcelize.Parcelize
import net.sharetrip.hotel.booking.model.PromotionalCoupon

@Parcelize
data class ListOfCoupon(var couponList: List<PromotionalCoupon>) : Parcelable