package net.sharetrip.bus.booking.model

import android.os.Parcelable
import kotlinx.parcelize.Parcelize

@Parcelize
data class BoardingPoints(
    val counterName: String,
    val reportingBranchId: Int,
    val reportingDate: String?,
    val reportingTime: String?,
    val scheduleTime: String?,
    var isSelected: Boolean = false
) : Parcelable
