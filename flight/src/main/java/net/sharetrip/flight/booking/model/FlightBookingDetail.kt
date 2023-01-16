package net.sharetrip.flight.booking.model

import com.squareup.moshi.Json

data class FlightBookingDetail(
    @Json(name = "searchId")
    var searchId: String = "",
    @Json(name = "sequenceCode")
    var sequenceCode: String = "",
    @Json(name = "sessionId")
    var sessionId: String = "",
    @Json(name = "specialNote")
    var specialNote: String = "",
    @Json(name = "gateWay")
    var gateWay: String = "",
    @Json(name = "cardSeries")
    var cardSeries: String = "",
    @Json(name = "coupon")
    var coupon: String = "",
    @Json(name = "tripCoin")
    var tripCoin: Int = 0,
    @Json(name = "passengers")
    var passengers: Passengers = Passengers(),
    @Json(name = "redeemPoints")
    var redeemPoints: String = "",
    var verifiedMobileNumber: String? = "",
    var otp: String? = ""
)
