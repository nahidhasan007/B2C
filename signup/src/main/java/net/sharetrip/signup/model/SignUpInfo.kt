package net.sharetrip.signup.model

import com.sharetrip.base.utils.ShareTripAppConstants.PASSWORD_MIN_CHAR_LEN
import net.sharetrip.shared.utils.isEmailValid
import net.sharetrip.shared.utils.isPasswordMatchesInstruction
import net.sharetrip.shared.utils.isPasswordValid
import net.sharetrip.shared.utils.isPhoneNumberValid
import com.squareup.moshi.Json

data class SignUpInfo(
    @Json(name = "email")
    var email: String = "",
    @Json(name = "password")
    var password: String = "",
    var confirmPassword: String = "",
    @Json(name = "referralCode")
    var referralCode: String = "",
    var mobileNumber: String? = null

) {
    val isEmailValid: Boolean
        get() = email.isEmailValid()

    val isPasswordMatched: Boolean
        get() = password == confirmPassword

    val isPasswordValid: Boolean
        get() = password.isPasswordValid(PASSWORD_MIN_CHAR_LEN)

    val isPasswordMatchInstruction: Boolean
        get() = password.isPasswordMatchesInstruction()

    val isMobileNumberValid: Boolean
        get() = mobileNumber.isNullOrEmpty() || mobileNumber.isPhoneNumberValid()

    val isInputDataValid: Boolean
        get() = isEmailValid && isPasswordMatched && isPasswordValid && isPasswordMatchInstruction && isMobileNumberValid

}
