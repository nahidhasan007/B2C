package net.sharetrip.flight.booking.model

import android.os.Parcelable
import kotlinx.parcelize.Parcelize

@Parcelize
data class TravellersInfo(
    val adult: Int,
    val child: Int,
    val infant: Int,
    val classType: String,
    var tripDate: String,
    val childDateOfBirthList: ArrayList<ChildrenDOB>
) : Parcelable
