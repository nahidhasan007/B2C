package net.sharetrip.bus.booking.model

import android.os.Parcel
import android.os.Parcelable
import com.squareup.moshi.Json
import net.sharetrip.bus.booking.model.covidtest.Covid
import net.sharetrip.bus.booking.model.covidtest.CovidTestOption

class ItemTraveler : Parcelable {
    @Json(name = "titleName")
    var titleName: String? = null

    @Json(name = "givenName")
    var givenName: String? = null

    @Json(name = "surName")
    var surName: String? = null

    @Json(name = "nationality")
    var nationality: String? = null

    @Json(name = "dateOfBirth")
    var dateOfBirth: String? = null

    @Json(name = "gender")
    var gender: String? = null

    @Json(name = "passportNumber")
    var passportNumber: String? = null

    @Json(name = "frequentFlyerNumber")
    var frequentFlyerNumber: String? = null

    @Json(name = "passportExpireDate")
    var passportExpireDate: String? = null

    @Json(name = "email")
    var email: String? = null

    @Json(name = "mobileNumber")
    var mobileNumber: String? = null

    @field:Json(name = "mealPreference")
    var mealPreference: String = ""

    @field:Json(name = "wheelChair")
    var wheelChair: String = ""

    @Json(name = "passportCopy")
    var passportCopy: String? = null

    @Json(name = "visaCopy")
    var visaCopy: String? = null

    @Json(name = "country")
    var country: String? = null
    var isValidData = false

    @PassengerType
    var passengerType = 0
    var passengerTypeTitle: String? = null
    var mealPreferenceText: String? = "None"
    var wheelChairText: String? = "None"
    var covid: Covid? = null
    var luggageCode: List<String> = arrayListOf()

    @Transient
    var covidTestOption: CovidTestOption? = null

    val passengerTypeInText: String
        get() = when (passengerType) {
            1 -> "Adult(Primary)"
            2 -> "Adult "
            3 -> "Child"
            else -> "Infant"
        }

    constructor() {}

    constructor(@PassengerType mPassengerType: Int, mPassengerTypeTitle: String?) {
        passengerTypeTitle = mPassengerTypeTitle
        passengerType = mPassengerType
        isValidData = false
    }

    override fun describeContents(): Int {
        return 0
    }

    override fun writeToParcel(dest: Parcel, flags: Int) {
        dest.writeString(titleName)
        dest.writeString(givenName)
        dest.writeString(surName)
        dest.writeString(nationality)
        dest.writeString(dateOfBirth)
        dest.writeString(gender)
        dest.writeString(passportNumber)
        dest.writeString(frequentFlyerNumber)
        dest.writeString(passportExpireDate)
        dest.writeString(email)
        dest.writeString(mobileNumber)
        dest.writeByte(if (isValidData) 1.toByte() else 0.toByte())
        dest.writeInt(passengerType)
        dest.writeString(passengerTypeTitle)
        dest.writeString(passportCopy)
        dest.writeString(visaCopy)
        dest.writeString(country)
    }

    constructor(`in`: Parcel) {
        titleName = `in`.readString()
        givenName = `in`.readString()
        surName = `in`.readString()
        nationality = `in`.readString()
        dateOfBirth = `in`.readString()
        gender = `in`.readString()
        passportNumber = `in`.readString()
        frequentFlyerNumber = `in`.readString()
        passportExpireDate = `in`.readString()
        email = `in`.readString()
        mobileNumber = `in`.readString()
        isValidData = `in`.readByte().toInt() != 0
        passengerType = `in`.readInt()
        passengerTypeTitle = `in`.readString()
        passportCopy = `in`.readString()
        visaCopy = `in`.readString()
        country = `in`.readString()
    }

    companion object CREATOR : Parcelable.Creator<ItemTraveler> {
        override fun createFromParcel(parcel: Parcel): ItemTraveler {
            return ItemTraveler(parcel)
        }

        override fun newArray(size: Int): Array<ItemTraveler?> {
            return arrayOfNulls(size)
        }
    }
}
