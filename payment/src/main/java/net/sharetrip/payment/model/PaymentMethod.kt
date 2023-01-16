package net.sharetrip.payment.model

import com.squareup.moshi.Json

data class PaymentMethod(
    var id: String,
    var name: String,
    var code: String,
    var logo: Logo,
    var checked: Boolean = false,
    var series: List<Series>,
    @field:Json(name = "coupon_applicable")
    var isCouponAvailable: Boolean = false,
    @field:Json(name = "redeem_tripcoin_applicable")
    var isRedeemTripCoinApplicable: Boolean = false,
    @field:Json(name = "earn_tripcoin_applicable")
    var isEarnTripCoinApplicable: Boolean = false,
    var currency: Currency,
    var charge: Double
)
