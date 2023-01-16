package net.sharetrip.visa.history.model

import android.os.Parcelable
import kotlinx.android.parcel.Parcelize

@Parcelize
data class PrimaryContactItem(
    val surName: String?,
    val mobileNumber: String?,
    val localAddress: String?,
    val givenName: String?,
    val email: String?,
    val specialNotes: String?
) : Parcelable