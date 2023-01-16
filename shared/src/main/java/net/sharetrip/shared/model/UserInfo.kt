package net.sharetrip.shared.model

data class UserInfo(
        var email: String,
        var mobileNumber: String,
        var avatar: String ="",
        var username: String,
        var uuid: String,
        var firstName: String,
        var lastName: String,
        var token: String
)
