package net.sharetrip.bus.booking.model

import android.os.Parcelable
import kotlinx.parcelize.Parcelize

@Parcelize
data class Company(
    val companySKU: String,
    val id: String,
    var name: String,
    val tenantId: String
) : Parcelable
