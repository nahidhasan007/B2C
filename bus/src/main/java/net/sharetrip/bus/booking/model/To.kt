package net.sharetrip.bus.booking.model

import android.os.Parcelable
import kotlinx.parcelize.Parcelize

@Parcelize
data class To(
    val code: String,
    val id: String,
    val name: String
) : Parcelable
