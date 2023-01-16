package net.sharetrip.visa.booking.model

import android.os.Parcelable
import kotlinx.android.parcel.Parcelize

@Parcelize
class VisaItemTraveler : Parcelable {
    var givenName: String? = ""
    var surName: String? = ""
    var nationality: String? = ""
    var dateOfBirth: String? = ""
    var gender: String? = ""
    var profession: String? = ""
    var passportNumber: String? = ""
    var passportExpireDate: String? = "0000"
    var email: String? = ""
    var mobileNumber: String? = ""
    var localAddress: String? = ""
    var foreignAddress: String? = ""
    var primaryContact: String? = "Yes"
    var specialNotes: String? = ""
    var requireDoc: ArrayList<HashMap<String, String>>? = ArrayList()
}
