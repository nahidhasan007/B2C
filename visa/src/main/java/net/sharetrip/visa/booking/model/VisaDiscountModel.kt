package net.sharetrip.visa.booking.model

import android.os.Parcelable
import kotlinx.android.parcel.Parcelize

@Parcelize
class VisaDiscountModel(
    var type: String = "Card",
    var discountAmount: Int = 0,
    var coupon: String = ""
) : Parcelable
