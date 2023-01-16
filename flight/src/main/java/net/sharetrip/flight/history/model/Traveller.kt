package net.sharetrip.flight.history.model

import android.os.Parcelable

import com.squareup.moshi.Json
import kotlinx.android.parcel.Parcelize

@Parcelize
data class Traveller(
        @Json(name = "type")
        var type: String? = null,
        @Json(name = "bookingCode")
        var bookingCode: String? = null,
        @Json(name = "title")
        var titleName: String? = null,
        @Json(name = "givenName")
        var givenName: String? = null,
        @Json(name = "surName")
        var surName: String? = null,
        @Json(name = "nationality")
        var nationality: String? = null,
        @Json(name = "dateOfBirth")
        var dateOfBirth: String? = null,
        @Json(name = "gender")
        var gender: String? = null,
        @Json(name = "travellerType")
        var travellerType: String? = null,
        @Json(name = "address1")
        var address1: String? = null,
        @Json(name = "passportNumber")
        var passportNumber: String? = null,
        @Json(name = "frequentFlyerNumber")
        var frequentFlyerNumber: String? = null,
        @Json(name = "passportExpireDate")
        private var passportExpireDate: String? = null,
        @Json(name = "email")
        var email: String? = null,
        @Json(name = "mobileNumber")
        var mobileNumber: String? = null,
        @Json(name = "primaryContact")
        var primaryContact: String? = null,
        @Json(name = "passportCopy")
        var passportCopy: String? = null,
        @Json(name = "visaCopy")
        var visaCopy: String? = null,
        @Json(name = "code")
        var code: String? = null,
        @Json(name = "covid")
        var covid: CovidHistory? = null,
        val paxAssociated: String,
        val paxNumber: String,
        val eTicket: String

) : Parcelable {
    fun getPassportExpireDate(): String {
        if (passportExpireDate!!.contains("T")) {
            val arr = passportExpireDate!!.split("T".toRegex()).dropLastWhile { it.isEmpty() }.toTypedArray()
            return arr[0]
        }
        return passportExpireDate as String
    }

    fun setPassportExpireDate(passportExpireDate: String) {
        this.passportExpireDate = passportExpireDate
    }


}
