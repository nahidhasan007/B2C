package net.sharetrip.shared.model.user

import com.squareup.moshi.Json

data class CoinSettings (
    @Json(name = "registrationEarnCoin")
    var registrationEarnCoin: Int,
    @Json(name = "referCoin")
    var referCoin: Int,
    @Json(name = "treasureBoxCoin")
    var treasureBoxCoin: Int,
    @Json(name = "minCostPlayWheel")
    var minCostPlayWheel: Int,
    @Json(name = "maxTripCoinValue")
    var maxTripCoinValue: Int
)
