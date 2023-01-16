package net.sharetrip.flight.history.model

import android.os.Parcelable
import kotlinx.android.parcel.Parcelize

@Parcelize
data class CovidHistory(
    val address: String,
    val center: String = "",
    val customerAddress: String,
    val discountPrice: Double,
    val isHomeCollection: Boolean,
    val option: String = ""
): Parcelable