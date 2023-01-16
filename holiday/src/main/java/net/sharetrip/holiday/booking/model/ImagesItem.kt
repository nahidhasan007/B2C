package net.sharetrip.holiday.booking.model

import android.os.Parcelable
import kotlinx.android.parcel.Parcelize

@Parcelize
data class ImagesItem(
    val srcLarge: String,
    val srcMedium: String,
    val srcThumb: String
) : Parcelable
