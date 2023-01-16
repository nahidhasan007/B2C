package net.sharetrip.holiday.history.model

import android.os.Parcelable
import kotlinx.android.parcel.Parcelize

@Parcelize
data class BookedPackageInfo(
    val title: String,
    val cancellationPolicy: String,
    val highlights: String,
    val itinerary: String,
    val includedService: String,
    val excludedService: String,
    val notes: String,
    val cxlPolicy: String,
    val generalCondition: String,
    val pickupNotes: String,
    val featuredText: String,
    val tax: String
) : Parcelable
