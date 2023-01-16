package net.sharetrip.bus.booking.model

import android.os.Parcelable
import kotlinx.parcelize.Parcelize

@Parcelize
data class CompanyName(
    val companyName: String,
    val count: Int,
    val companyId: String
) : Parcelable
