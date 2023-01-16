package net.sharetrip.bus.history.model

import android.os.Parcelable
import kotlinx.parcelize.Parcelize

@Parcelize
data class SeatInHistory(
    val colorCode: String,
    val deckTitle: String?,
    val fare: String,
    val seatId: Int,
    val seatNo: String,
    val seatTypeId: String,
    val status: String,
    val xaxis: Int,
    val yaxis: Int
):Parcelable
