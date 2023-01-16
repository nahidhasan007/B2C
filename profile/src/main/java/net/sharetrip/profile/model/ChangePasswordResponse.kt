package net.sharetrip.profile.model

data class ChangePasswordResponse (
    var code: String,
    var response: Response,
    var message: String
)
