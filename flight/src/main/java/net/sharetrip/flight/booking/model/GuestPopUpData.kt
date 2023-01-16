package net.sharetrip.flight.booking.model

import androidx.annotation.DrawableRes
import androidx.annotation.StringRes

data class GuestPopUpData (
        @StringRes
        val title: Int,
        @StringRes
        val detail: Int,
        @DrawableRes val icon: Int,
        val listener: GuestLoginListener?
)
