package net.sharetrip.tour.model

import android.os.Parcelable
import kotlinx.android.parcel.Parcelize

@Parcelize
data class TourSummary(
    val offerNo: String,
    val adult: Int = 0,
    val childTotal: Int = 0,
    val child3To6: Int = 0,
    val child7To12: Int = 0,
    val infant: Int = 0,
    val adultCost: Int = 0,
    val child3To6Cost: Int = 0,
    val child7To12Cost: Int = 0,
    val infantCost: Int = 0,
    val departurePoint: String,
    val city: String,
    val country: String,
    val timePeriod: String,
    val date: String,
    val totalCost: Int,
    val earnTripCoin: Int,
    var cancelPolicy: String = "",
    var hasCancelPolicy: Boolean = false,
    var cancelPolicyDate: String = ""
) : Parcelable
