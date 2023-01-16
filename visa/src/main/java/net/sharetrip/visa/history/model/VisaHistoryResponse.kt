package net.sharetrip.visa.history.model

import android.os.Parcelable
import kotlinx.android.parcel.Parcelize

@Parcelize
data class VisaHistoryResponse(
    val data: List<VisaHistoryItem> = ArrayList(),
    var offset: Int,
    var count: Int,
    var limit: Int = 0
) : Parcelable