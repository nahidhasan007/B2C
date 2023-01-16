package net.sharetrip.visa.booking.model

import android.os.Parcelable
import kotlinx.android.parcel.Parcelize

@Parcelize
data class NationalityCode(
    var code: String,
    var name: String,
) : Parcelable
