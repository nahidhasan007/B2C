package net.sharetrip.tour.model

import android.os.Parcelable
import kotlinx.parcelize.Parcelize

@Parcelize
data class BookedTransfer(
    val cancellationPolicy: String? = null,
    val currencyCode: String? = null,
    val generalCondition: String? = null,
    val itinerary: String? = null,
    val notes: String? = null,
    val pickupNotes: String? = null,
    val tax: String? = null,
    val title: String? = null
) : Parcelable
