package net.sharetrip.hotel.booking.model

import android.os.Parcelable
import android.text.TextUtils
import net.sharetrip.shared.utils.isEmailValid
import com.squareup.moshi.Json
import kotlinx.parcelize.Parcelize
import net.sharetrip.shared.utils.PHONE_VALIDATION_REGEX
import net.sharetrip.shared.utils.RegexValidation.validRegex

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
                && isPhoneNumberValid()
                && email.isEmailValid())

     fun isPhoneNumberValid() : Boolean{
        return validRegex(phoneNumber, PHONE_VALIDATION_REGEX)
    }

    fun setDefaultValue() {
        if (userTitle == null) userTitle = ""
        if (givenName == null) givenName = ""
        if (surName == null) surName = ""
        if (phoneNumber == null) phoneNumber = ""
        if (email == null) email = ""
        if (address1 == null) address1 = ""
        if (address2 == null) address2 = ""
    }
}
