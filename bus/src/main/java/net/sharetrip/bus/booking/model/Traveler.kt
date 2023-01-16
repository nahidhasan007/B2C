package net.sharetrip.bus.booking.model

import android.os.Parcelable
import com.squareup.moshi.Json
import kotlinx.android.parcel.Parcelize

@Parcelize
data class Traveler(

    @field:Json(name = "titleName")
    var titleName: String = "",

    @field:Json(name = "givenName")
    var givenName: String = "",

    @field:Json(name = "surName")
    var surName: String = "",

    @field:Json(name = "gender")
    var gender: String = "",

    @field:Json(name = "nationality")
    var nationality: String = "",

    @field:Json(name = "dateOfBirth")
    var dateOfBirth: String = "",

    @field:Json(name = "passportNumber")
    var passportNumber: String = "",

    @field:Json(name = "passportCopy")
    var passportCopy: String = "",

    @field:Json(name = "passportExpireDate")
    var passportExpireDate: String = "",

    @field:Json(name = "visaCopy")
    var visaCopy: String = "",

    @field:Json(name = "frequentFlyerNumber")
    var frequentFlyerNumber: String = "",

    @field:Json(name = "quickPick")
    var isQuickPick: Boolean = false,

    @field:Json(name = "travellerType")
    var travellerType: String? = null,

    @field:Json(name = "code")
    var code: String? = null,

    @Json(name = "mobileNumber")
    var mobileNumber: String? = "",

    @Json(name = "email")
    var email: String? = ""

) : Parcelable
