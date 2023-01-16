package net.sharetrip.hotel.booking.model.payment

import androidx.annotation.StringDef

const val EARN = "earnTC"
const val REDEEM = "redeemTC"
const val COUPON = "useCoupon"

@kotlin.annotation.Retention(AnnotationRetention.SOURCE)
@StringDef(EARN, REDEEM, COUPON)
annotation class FlightDiscountOptionEnum