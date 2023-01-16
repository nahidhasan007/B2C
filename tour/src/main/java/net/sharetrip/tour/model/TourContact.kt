package net.sharetrip.tour.model

import android.os.Parcelable
import android.text.TextUtils
import net.sharetrip.shared.utils.isEmailValid
import com.squareup.moshi.Json
import kotlinx.parcelize.Parcelize

@Parcelize
data class TourContact(
    @Json(name = "titleName")
    var titleName: String? = null,

    @Json(name = "givenName")
    var givenName: String? = null,

    @Json(name = "surName")
    var surName: String? = null,

    @Json(name = "email")
    var email: String? = null,

    @Json(name = "address1")
    var address1: String? = null,

    @Json(name = "mobileNumber")
    var mobileNumber: String? = null,

    @Json(name = "dateOfBirth")
    var dateOfBirth: String? = null,

    private var value: String = ""
) : Parcelable {
    val isInputDataValid: Boolean
        get() = (!TextUtils.isEmpty(titleName)
                && !TextUtils.isEmpty(givenName)
                && !TextUtils.isEmpty(surName)
                && !TextUtils.isEmpty(mobileNumber)
                && email.isEmailValid()
                && !TextUtils.isEmpty(dateOfBirth)
                && !TextUtils.isEmpty(address1))

    fun setDefaultValue() {
        if (titleName == null) titleName = value
        if (givenName == null) givenName = ""
        if (mobileNumber == null) mobileNumber = value
        if (email == null) email = value
        if (dateOfBirth == null) dateOfBirth = value
        if (address1 == null) address1 = value
    }
}
