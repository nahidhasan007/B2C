package net.sharetrip.flight.booking.model

import android.os.Parcelable
import kotlinx.android.parcel.Parcelize

@Parcelize
data class CovidTestOption(
        val isAddress: Boolean = false,
        var code: String = "",
        val price: Double = 0.0,
        var name: String = "",
        val discountPrice: Double = 0.0,
        var isSelected: Boolean = false,
        var testCode: String = "",
        var addressDetails: String = "",
        var testCenterName: String = "",
        var logo: String = "",
        var description: String = "",
        var self: Boolean = true
) : Parcelable