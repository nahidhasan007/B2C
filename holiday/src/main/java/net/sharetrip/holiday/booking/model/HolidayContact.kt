package net.sharetrip.holiday.booking.model

import android.os.Parcel
import android.os.Parcelable
import android.text.TextUtils
import net.sharetrip.shared.utils.isEmailValid

import com.squareup.moshi.Json

class HolidayContact : Parcelable {
    @Json(name = "titleName")
    var titleName: String? = null

    @Json(name = "givenName")
    var givenName: String? = null

    @Json(name = "surName")
    var surName: String? = null

    @Json(name = "email")
    var email: String? = null

    @Json(name = "address1")
    var address1: String? = null

    @Json(name = "mobileNumber")
    var mobileNumber: String? = null

    @Json(name = "dateOfBirth")
    var dateOfBirth: String? = null

    private var value = ""

    val isInputDataValid: Boolean
        get() = (!TextUtils.isEmpty(titleName)
                && !TextUtils.isEmpty(givenName)
                && !TextUtils.isEmpty(surName)
                && !TextUtils.isEmpty(mobileNumber)
                && email.isEmailValid()
                && !TextUtils.isEmpty(dateOfBirth)
                && !TextUtils.isEmpty(address1))

    protected constructor(`in`: Parcel) {
        titleName = `in`.readString()
        givenName = `in`.readString()
        surName = `in`.readString()
        email = `in`.readString()
        address1 = `in`.readString()
        mobileNumber = `in`.readString()
        dateOfBirth = `in`.readString()
        value = `in`.readString()!!
    }

    fun setDefaultValue() {
        if (titleName == null) titleName = value
        if (givenName == null) givenName = ""
        if (mobileNumber == null) mobileNumber = value
        if (email == null) email = value
        if (dateOfBirth == null) dateOfBirth = value
        if (address1 == null) address1 = value
    }

    override fun describeContents(): Int {
        return 0
    }

    override fun writeToParcel(dest: Parcel, flags: Int) {
        dest.writeString(titleName)
        dest.writeString(givenName)
        dest.writeString(surName)
        dest.writeString(email)
        dest.writeString(address1)
        dest.writeString(mobileNumber)
        dest.writeString(dateOfBirth)
        dest.writeString(value)
    }

    companion object {
        @JvmField
        val CREATOR: Parcelable.Creator<HolidayContact> =
            object : Parcelable.Creator<HolidayContact> {
                override fun createFromParcel(`in`: Parcel): HolidayContact {
                    return HolidayContact(`in`)
                }

                override fun newArray(size: Int): Array<HolidayContact?> {
                    return arrayOfNulls(size)
                }
            }
    }
}
