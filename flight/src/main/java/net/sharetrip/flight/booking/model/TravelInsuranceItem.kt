package net.sharetrip.flight.booking.model

import android.os.Parcelable
import kotlinx.parcelize.Parcelize

@Parcelize
data class TravelInsuranceItem(
    val code: String,
    var name: String,
    val options: List<TravelInsuranceOption>,
    val logo: String,
    val self: Boolean,
    var selectedItem: Int,
    var isShowAddressField: Boolean = false
) : Parcelable