package net.sharetrip.profile.model

data class TripCoinWinner(
    var type: String,
    var activity: String,
    var note: String,
    var value: Int = 0,
    var winingDate: String,
    var expireDate: String,
    var name: String,
    var avatar: String
)
