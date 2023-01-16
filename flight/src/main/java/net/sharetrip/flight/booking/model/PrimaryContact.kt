package net.sharetrip.flight.booking.model

import android.os.Parcelable
import android.text.TextUtils
import net.sharetrip.shared.utils.isEmailValid
import com.squareup.moshi.Json
import kotlinx.parcelize.Parcelize

@Parcelize
data class PrimaryContact(
    @field:Json(name = "titleName")
    var userTitle: String = "",
    @field:Json(name = "givenName")
    var givenName: String = "",
    @field:Json(name = "surName")
    var surName: String = "",
    @field:Json(name = "mobileNumber")
    var phoneNumber: String = "",
    @field:Json(name = "nationality")
    var nationality: String = "",
    @field:Json(name = "email")
    var email: String = "",
    @field:Json(name = "dateOfBirth")
    var dateOfBirth: String = "",
    @field:Json(name = "address1")
    var address1: String = "",
    @field:Json(name = "address2")
    var address2: String = ""

) : Parcelable {

    val fullName: String
        get() = "$userTitle $givenName $surName"

    val isInputDataValid: Boolean
        get() = (!TextUtils.isEmpty(userTitle)
                && !TextUtils.isEmpty(givenName)
                && !TextUtils.isEmpty(surName)
                && !TextUtils.isEmpty(phoneNumber)
                && email.isEmailValid())

}