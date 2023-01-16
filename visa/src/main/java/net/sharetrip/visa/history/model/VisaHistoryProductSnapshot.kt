package net.sharetrip.visa.history.model

import android.os.Parcelable
import kotlinx.android.parcel.Parcelize

@Parcelize
data class VisaHistoryProductSnapshot(
    val title: String? = null,
    val validityDays: Int? = null,
    val maxStayDays: Int? = null
) : Parcelable