package net.sharetrip.flight.booking.model.user

import com.squareup.moshi.Json

data class User (
        @field:Json(name = "titleName")
        var title: String,
        @field:Json(name = "givenName")
        var firstName: String?,
        @field:Json(name = "surName")
        var lastName: String,
        @Json(name = "designation")
        var designation: String,
        @Json(name = "address")
        var address: String,
        @Json(name = "mobileNumber")
        var mobileNumber: String,
        @Json(name = "avatar")
        var avatar: String,
        @Json(name = "gender")
        var gender: String,
        @Json(name = "dob")
        var dob: String,
        @Json(name = "username")
        var username: String,
        @Json(name = "email")
        var email: String,
        @Json(name = "totalPoints")
        var totalPoints: Int = 0,
        @Json(name = "totalSpend")
        var totalSpend: Int = 0,
        @Json(name = "remainingPoints")
        var remainingPoints: Int = 0,
        @Json(name = "redeemablePoints")
        var redeemablePoints: Int = 0,
        @Json(name = "gamificationPoints")
        var gamificationPoints: Int = 0,
        @Json(name = "referralCode")
        var referralCode: String,
        @field:Json(name = "profileLevel")
        var userLevel: String,
        @Json(name = "coinSettings")
        var coinSettings: CoinSettings
)
