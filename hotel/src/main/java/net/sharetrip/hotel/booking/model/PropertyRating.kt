package net.sharetrip.hotel.booking.model

import android.os.Parcelable
import kotlinx.android.parcel.Parcelize

@Parcelize
data class PropertyRating(
    var rating: Int = 0,
    var isSelected: Boolean = false
) : Parcelable
