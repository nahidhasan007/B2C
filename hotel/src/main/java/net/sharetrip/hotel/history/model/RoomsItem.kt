package net.sharetrip.hotel.history.model

import android.os.Parcelable
import com.squareup.moshi.Json
import kotlinx.parcelize.Parcelize

@Parcelize
data class RoomsItem(
    @Json(name = "titleName")
    var titleName: String? = "",

    @Json(name = "surName")
    var surName: String? = "",

    @Json(name = "address1")
    var address1: String? = "",

    @Json(name = "mobileNumber")
    var mobileNumber: String? = "",

    @Json(name = "givenName")
    var givenName: String? = "",

    @Json(name = "email")
    var email: String? = "",

    @Json(name = "nationality")
    var nationality: String? = "BD",

    @Json(name = "age")
    var age: String?,

    @Json(name = "travellerType")
    var travellerType: String?

) : Parcelable {

    val fullName: String
        get() {
            return "$titleName $givenName $surName"
        }
}
