package net.sharetrip.bus.booking.model

import android.os.Parcelable
import kotlinx.android.parcel.Parcelize
import net.sharetrip.bus.booking.model.covidtest.CovidTestOption

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
