package net.sharetrip.holiday.booking.model

import android.os.Parcelable
import net.sharetrip.shared.utils.Strings.isProperEmail
import kotlinx.android.parcel.Parcelize

@Parcelize
data class PrimaryContact(
    var titleName: String? = "",
    var surName: String? = "",
    var givenName: String? = "",
    var mobileNumber: String? = "",
    var email: String? = "",
    var address1: String? = ""
) : Parcelable {
    fun isDataValid() =
        (titleName!!.isNotEmpty() && givenName!!.isNotEmpty() && surName!!.isNotEmpty()
                && mobileNumber!!.isNotEmpty() && email!!.isNotEmpty() && address1!!.isNotEmpty() && isProperEmail(
            email
        ))
}
