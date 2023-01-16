package net.sharetrip.profile.model

import net.sharetrip.shared.model.GuestLoginListener

data class UserConfirmPopUpData (
    val title: String,
    val detail: String,
    val imgUrl: String = "",
    val listener: GuestLoginListener?
)