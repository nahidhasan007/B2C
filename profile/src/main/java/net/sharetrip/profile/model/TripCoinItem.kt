package net.sharetrip.profile.model

data class TripCoinItem (
    var pointsEarned: Int = 0,
    var expiredDate: String,
    var type: String? = null
)
