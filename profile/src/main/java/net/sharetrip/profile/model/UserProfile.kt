package net.sharetrip.profile.model

import net.sharetrip.shared.utils.DateUtil
import net.sharetrip.shared.utils.isPassportNumberValid
import com.squareup.moshi.Json

data class UserProfile(
        @field:Json(name = "country")
        var country: String = "",
        @field:Json(name = "surName")
        var surName: String = "",
        @field:Json(name = "gender")
        var gender: String = "",
        @field:Json(name = "mealPreference")
        var mealPreference: String = "",
        @field:Json(name = "redeemablePoints")
        var redeemablePoints: Int = 0,
        @field:Json(name = "mobileNumber")
        var mobileNumber: String = "",
        @field:Json(name = "otherPassengers")
        var otherPassengers: MutableList<Traveler> = ArrayList(),
        @field:Json(name = "seatPreference")
        var seatPreference: String = "",
        @field:Json(name = "passport")
        var passport: String = "",
        @field:Json(name = "referralCode")
        var referralCode: String = "",
        @field:Json(name = "passportCopy")
        var passportCopy: String = "",
        @field:Json(name = "email")
        var email: String = "",
        @field:Json(name = "passportNumber")
        var passportNumber: String = "",
        @field:Json(name = "address2")
        var address2: String = "",
        @field:Json(name = "address1")
        var address1: String = "",
        @field:Json(name = "givenName")
        var givenName: String = "",
        @field:Json(name = "dateOfBirth")
        var dateOfBirth: String = "",
        @field:Json(name = "avatar")
        var avatar: String = "",
        @field:Json(name = "titleName")
        var titleName: String = "",
        @field:Json(name = "nationality")
        var nationality: String = "",
        @field:Json(name = "totalPoints")
        var totalPoints: Int = 0,
        @field:Json(name = "passportExpireDate")
        var passportExpireDate: String = "",
        @field:Json(name = "postCode")
        var postCode: String = "",
        @field:Json(name = "designation")
        var designation: String = "",
        @field:Json(name = "visaCopy")
        var visaCopy: String = "",
        @field:Json(name = "profileLevel")
        var profileLevel: String = "",
        @field:Json(name = "username")
        var username: String = "",
        @field:Json(name = "frequentFlyerNumber")
        var frequentFlyerNumber: String = ""
) {
        fun isUserAgeValid() : Boolean {
                return DateUtil.getAge(dateOfBirth) <= DateUtil.MAX_VALID_AGE
        }

        fun isValidPassportOrEmpty() : Boolean{
                return passportNumber.isEmpty() || passportNumber.isPassportNumberValid()
        }
}
