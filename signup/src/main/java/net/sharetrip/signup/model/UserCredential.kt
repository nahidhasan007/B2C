package net.sharetrip.signup.model

import com.sharetrip.base.utils.ShareTripAppConstants.PASSWORD_MIN_CHAR_LEN
import net.sharetrip.shared.utils.isEmailValid
import net.sharetrip.shared.utils.isPasswordValid
import com.squareup.moshi.Json

data class UserCredential(
    @field:Json(name = "email")
    var emailId: String = "",
    @Json(name = "password")
    var password: String = ""
) {
    val isEmailValid: Boolean
        get() = emailId.isEmailValid()

    val isPasswordValid: Boolean
        get() = password.isPasswordValid(PASSWORD_MIN_CHAR_LEN)

    val isInputDataValid: Boolean
        get() = isEmailValid && isPasswordValid

}
