package net.sharetrip.flight.history.model

import android.os.Parcelable
import kotlinx.android.parcel.Parcelize

@Parcelize
data class BasicItem(
    val baggage: List<BaggageItem>,
    val origin: Origin,
    val destination: Destination
) : Parcelable