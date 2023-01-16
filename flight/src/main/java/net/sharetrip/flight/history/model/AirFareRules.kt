package net.sharetrip.flight.history.model

import android.os.Parcelable
import kotlinx.android.parcel.Parcelize

@Parcelize
data class AirFareRules(
        var type: String? = null,
        var rules: List<Rule>? = null
) : Parcelable