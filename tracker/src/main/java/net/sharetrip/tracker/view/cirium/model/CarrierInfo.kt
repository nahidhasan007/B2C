package net.sharetrip.tracker.view.cirium.model

import android.os.Parcelable
import kotlinx.parcelize.Parcelize

@Parcelize
data class CarrierInfo(
    val code: String = "",
    val number: String = ""
) : Parcelable
