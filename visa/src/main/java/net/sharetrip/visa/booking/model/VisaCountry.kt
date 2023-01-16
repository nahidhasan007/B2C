package net.sharetrip.visa.booking.model

import android.os.Parcelable
import kotlinx.android.parcel.Parcelize

@Parcelize
data class VisaCountry(
    val visaCountryCode: String = "",
    val countryName: String = "",
    val visaRequirement: String = "",
    val visaPrepMinDays: Int = 0
) : Parcelable
