package net.sharetrip.tour.model

import android.os.Parcelable
import net.sharetrip.shared.utils.Strings
import com.squareup.moshi.Json
import kotlinx.android.parcel.Parcelize

@Parcelize
data class PrimaryContact(
    @Json(name = "titleName")
    var titleName: String? = null,
    @Json(name = "surName")
    var surName: String? = null,
    @Json(name = "givenName")
    var givenName: String? = null,
    @Json(name = "mobileNumber")
    var mobileNumber: String? = null,
    @Json(name = "email")
    var email: String? = null,
    @Json(name = "address1")
    var address1: String? = null,
    @Json(name = "age")
    var age: Int = 0
) : Parcelable {

    val isDataValid: Boolean
        get() = (titleName!!.isNotEmpty() && givenName!!.isNotEmpty() && surName!!.isNotEmpty()
                && mobileNumber!!.isNotEmpty() && email!!.isNotEmpty() && address1!!.isNotEmpty() && age != 0
                && Strings.isProperEmail(email!!))
}
