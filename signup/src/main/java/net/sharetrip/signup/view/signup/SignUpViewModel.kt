package net.sharetrip.signup.view.signup

import androidx.databinding.ObservableBoolean
import androidx.lifecycle.MutableLiveData
import com.sharetrip.base.event.Event
import com.sharetrip.base.network.model.BaseResponse
import com.sharetrip.base.network.model.ErrorType
import com.sharetrip.base.network.model.RestResponse
import net.sharetrip.shared.utils.analytics.AnalyticsProvider
import com.sharetrip.base.viewmodel.BaseOperationalViewModel
import net.sharetrip.signup.model.SignUpInfo
import net.sharetrip.signup.network.SingUpApiService
import net.sharetrip.signup.utils.UtilText
import net.sharetrip.signup.utils.UtilText.SUCCESS
import net.sharetrip.signup.utils.UtilText.emptyConfirmPasswordField
import net.sharetrip.signup.utils.UtilText.emptyPasswordField
import net.sharetrip.signup.utils.UtilText.invalidEmailMsg
import net.sharetrip.signup.utils.UtilText.invalidPasswordMsg
import net.sharetrip.signup.utils.UtilText.invalidPhoneNumber
import net.sharetrip.signup.utils.UtilText.passwordInstruction
import net.sharetrip.signup.utils.UtilText.passwordNotMatched
import net.sharetrip.signup.utils.UtilText.undefinedErrorMsg

class SignUpViewModel(val apiService: SingUpApiService) : BaseOperationalViewModel() {
    val signUpInfo = SignUpInfo()
    var snackBarMsg = MutableLiveData<Event<String>>()
    var isSignUpSuccess = MutableLiveData<Event<Boolean>>()
    var isLoading = ObservableBoolean(false)
    var goToLogin = MutableLiveData<Event<Boolean>>()
    private val eventManager =
        AnalyticsProvider.eventManager(AnalyticsProvider.getFirebaseAnalytics())

    init {
        isLoading = ObservableBoolean(false)
    }

    fun onTextChange() {
        //notifyPropertyChanged(BR.signUpInfo)
    }

    fun onClickSignUp() {
        if (signUpInfo.isInputDataValid) {
            fetchSignUpResponse(signUpInfo)

        } else {
            snackBarMsg.value = Event(errorMsg(signUpInfo))
        }
    }

    private fun errorMsg(signUpInfo: SignUpInfo): String {
        return when {
            signUpInfo.email.isEmpty()->{
                UtilText.emptyEmailField
            }
            !signUpInfo.isEmailValid -> {
                invalidEmailMsg
            }
            signUpInfo.password.isEmpty() ->{
                emptyPasswordField
            }
            signUpInfo.confirmPassword.isEmpty() ->{
                emptyConfirmPasswordField
            }
            !signUpInfo.isPasswordMatched->{
                passwordNotMatched
            }
            !signUpInfo.isPasswordValid -> {
                invalidPasswordMsg
            }
            !signUpInfo.isPasswordMatchInstruction -> {
                passwordInstruction
            }
            !signUpInfo.mobileNumber.isNullOrEmpty() && !signUpInfo.isMobileNumberValid -> {
                invalidPhoneNumber
            }
            else -> {
                undefinedErrorMsg
            }
        }
    }

    private fun fetchSignUpResponse(signUpInfo: SignUpInfo) {
        isLoading.set(true)
        executeSuspendedCodeBlock(
            operationTag = emailSignup,
            codeBlock = { apiService.getEmailSignUpResult(signUpInfo) }
        )
    }

    fun onClickLogin() {
        goToLogin.value = Event(true)
    }

    override fun onSuccessResponse(operationTag: String, data: BaseResponse.Success<Any>) {
        when (operationTag) {
            emailSignup -> {
                val response = (data.body as RestResponse<*>)
                if (response.code == SUCCESS) {
                    eventManager.registrationSuccessB2c()
                    isSignUpSuccess.value = Event(true)
                } else {
                    eventManager.registrationErrorB2c()
                }
                isLoading.set(false)
            }
        }
    }

    override fun onAnyError(operationTag: String, errorMessage: String, type: ErrorType?) {
        isLoading.set(false)
        snackBarMsg.value = Event(errorMessage)
        eventManager.registrationErrorB2c()
    }

    private companion object {
        private const val emailSignup = "email_sign_up"
    }
}