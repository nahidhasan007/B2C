package net.sharetrip.flight.booking.model

import android.os.Parcelable
import kotlinx.parcelize.Parcelize

@Parcelize
data class BaggageInsurance(
    val code: String = "",
    val logo: String = "",
    val name: String = "",
    val options: List<BaggageInsuranceOption> = ArrayList(),
    val self: Boolean = false
) : Parcelable