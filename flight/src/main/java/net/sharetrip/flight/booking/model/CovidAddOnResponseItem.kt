package net.sharetrip.flight.booking.model

import android.os.Parcelable
import kotlinx.android.parcel.Parcelize

@Parcelize
data class CovidAddOnResponseItem(
    val code: String,
    var name: String,
    val options: List<CovidTestOption>,
    val self: Boolean,
    val logo: String,
    var selectedItem: Int,
    var isShowAddressField: Boolean = false
) : Parcelable