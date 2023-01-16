package net.sharetrip.flight.booking.model

import android.os.Parcelable
import kotlinx.parcelize.Parcelize

@Parcelize
data class ChildrenDOB(
    var title: String = "",
    var date: String = ""
) : Parcelable