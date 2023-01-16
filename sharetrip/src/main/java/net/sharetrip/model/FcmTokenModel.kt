package net.sharetrip.model

data class FcmTokenModel(
    var userId: String,
    var email: String,
    var token: String,
    var name: String
)