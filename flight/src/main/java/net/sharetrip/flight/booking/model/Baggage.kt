package net.sharetrip.flight.booking.model

import android.os.Parcelable
import kotlinx.android.parcel.Parcelize

@Parcelize
data class Baggage(
        val type: String,
        var adult: String? = null,
        var child: String? = null,
        var infant: String? = null
) : Parcelable
