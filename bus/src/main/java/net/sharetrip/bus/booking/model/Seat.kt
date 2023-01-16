package net.sharetrip.bus.booking.model

import android.os.Parcelable
import kotlinx.parcelize.Parcelize

@Parcelize
data class Seat(
    val colorCode: String,
    val fare: Fares,
    val seatId: Int,
    val seatNo: String,
    val seatTypeId: String,
    var status: String,
    val xaxis: Int,
    val yaxis: Int,
    var count: Int = 0,
    var isVisibleValues: Boolean?,
    var isVisibleClose: Boolean = false,
    var isVisibleSeatStatus: Boolean?
) : Parcelable
