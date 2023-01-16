package net.sharetrip.flight.booking.model

import android.os.Parcelable
import kotlinx.parcelize.Parcelize

@Parcelize
data class BaggageInsuranceOption(
    var code: String = "",
    var optionCode: String = "",
    val price: Double = 0.0,
    var name: String = "",
    val discountPrice: Double = 0.0,
    var isSelected: Boolean = false,
    var logo: String = "",
    var description: String = "",
    var default: Boolean = false
) : Parcelable