package net.sharetrip.flight.booking.model

import android.os.Parcelable
import kotlinx.android.parcel.Parcelize

@Parcelize
data class NationalityCode(
    var code: String,
    var name: String
) : Parcelable