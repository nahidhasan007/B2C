package net.sharetrip.bus.history.model

import android.os.Parcelable
import kotlinx.parcelize.Parcelize

@Parcelize
data class BusHistoryResponse(
    val history: List<HistoryDetails>,
    val limit: Int,
    val offset: Int,
    val length: Int
) : Parcelable
