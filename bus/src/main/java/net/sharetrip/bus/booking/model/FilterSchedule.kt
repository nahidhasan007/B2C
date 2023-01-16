package net.sharetrip.bus.booking.model

import android.os.Parcelable
import kotlinx.parcelize.Parcelize

@Parcelize
data class FilterSchedule(
    val from: String,
    val to: String,
    var scheduleType: String = "departure"
) : Parcelable
