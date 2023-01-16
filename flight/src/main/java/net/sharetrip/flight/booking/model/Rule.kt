package net.sharetrip.flight.booking.model

import android.os.Parcelable
import kotlinx.android.parcel.Parcelize

@Parcelize
data class Rule(
        var code: Int,
        var type: String,
        var text: String
) : Parcelable