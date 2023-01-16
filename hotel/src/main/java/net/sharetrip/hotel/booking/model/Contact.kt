package net.sharetrip.hotel.booking.model

import android.os.Parcelable
import com.squareup.moshi.Json
import kotlinx.android.parcel.Parcelize

@Parcelize
data class Contact(
    @field:Json(name = "address")
    var address: String? = null,
    @field:Json(name = "phone")
    var phone: String? = null,
    @field:Json(name = "postalCode")
    var postalCode: String? = null,
    @field:Json(name = "center")
    var centerPoint: Center? = null,
    @field:Json(name = "region")
    var region: Region,
    @field:Json(name = "email")
    var email: String? = null
) : Parcelable
