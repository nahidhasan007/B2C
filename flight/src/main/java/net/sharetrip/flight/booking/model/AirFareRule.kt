package net.sharetrip.flight.booking.model

import android.os.Parcelable
import kotlinx.android.parcel.Parcelize

@Parcelize
data class AirFareRule(
        var type: String? = null,
        var rules: List<Rule>? = null
) : Parcelable