package net.sharetrip.visa.history.model

import android.os.Parcelable
import kotlinx.android.parcel.Parcelize

@Parcelize
data class VisaBookingUploadedDocsItem(
    val docUrl: String?,
    val docTitle: String?
) : Parcelable