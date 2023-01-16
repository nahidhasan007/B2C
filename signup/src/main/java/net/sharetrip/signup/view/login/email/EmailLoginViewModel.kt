package net.sharetrip.signup.view.login.email

import androidx.databinding.ObservableField
import androidx.lifecycle.MutableLiveData
import com.sharetrip.base.data.PrefKey
import com.sharetrip.base.data.SharedPrefsHelper
import com.sharetrip.base.event.Event
import net.sharetrip.shared.model.UserInfo
import com.sharetrip.base.network.model.BaseResponse
import com.sharetrip.base.network.model.ErrorType
import com.sharetrip.base.network.model.RestResponse
import com.sharetrip.base.viewmodel.BaseOperationalViewModel
import com.squareup.moshi.Moshi
import net.sharetrip.signup.model.UserCredential
import net.sharetrip.signup.network.SingUpApiService
import net.sharetrip.signup.utils.UtilText.SUCCESS
import net.sharetrip.signup.utils.UtilText.emptyEmailField
import net.sharetrip.signup.utils.UtilText.emptyPasswordField
import net.sharetrip.signup.utils.UtilText.invalidEmailMsg
import net.sharetrip.signup.utils.UtilText.invalidPasswordMsg
import net.sharetrip.signup.utils.UtilText.undefinedErrorMsg
import javax.inject.Inject
import javax.inject.Named

class EmailLoginViewModel(
    val apiService: SingUpApiService,
    val sharedPrefsHelper: SharedPrefsHelper
) : BaseOperationalViewModel() {

    var showMsg = ObservableField<String>()
    val credential = ObservableField(UserCredential())
    val goToForgetPassword = MutableLiveData<Event<Boolean>>()
    val goToSignup = MutableLiveData<Event<Boolean>>()
    val setActivityResult = MutableLiveData<Boolean>()
    val navigateBack = MutableLiveData<Event<Boolean>>()

    @JvmField
    @field:[Inject Named("result")]
    var isActivityResult: Boolean = false

    fun afterTextChanged() {
        credential.notifyChange()
    }

    fun onClickLogin() {
        credential.notifyChange()
        credential.get()?.let {
            if (it.isInputDataValid) {
                checkUserCredential()
            } else {
                showMsg.set(errorMsg(it))
                showMsg.notifyChange()
            }
        }

    }

    fun onClickSkip() {
        navigateBack.value = Event(true)
    }

    private fun checkUserCredential() {
        credential.get()?.let {
            executeSuspendedCodeBlock(
                operationTag = emailLogin,
                codeBlock = { apiService.getEmailLoginResponse(it) }
            )
        }
    }

    fun onClickForgetPassword() {
        goToForgetPassword.value = Event(true)
    }

    fun onClickSignUp() {
        goToSignup.value = Event(true)
    }

    override fun onSuccessResponse(operationTag: String, data: BaseResponse.Success<Any>) {
        when (operationTag) {
            emailLogin -> {
                val response = (data.body as RestResponse<*>)
                if (response.code == SUCCESS) {
                    val moshi = Moshi.Builder().build()
                    val jsonAdapter = moshi.adapter(UserInfo::class.java)
                    val userInfo = jsonAdapter.toJson(response.response as UserInfo)
                    val userInfoResponse = response.response as UserInfo
                    sharedPrefsHelper.put(
                        PrefKey.ACCESS_TOKEN,
                        (response.response as UserInfo).token
                    )
                    sharedPrefsHelper.put(PrefKey.USER_LAST_NAME, userInfoResponse.lastName)
                    sharedPrefsHelper.put(PrefKey.USER_PROFILE, userInfo)
                    sharedPrefsHelper.put(
                        PrefKey.USER_AVATER,
                        (response.response as UserInfo).avatar
                    )
                    sharedPrefsHelper.put(PrefKey.IS_LOGIN, true)
                    setActivityResult.value = true

                } else {
                    showMsg.set(response.message)
                }
            }
        }
    }

    override fun onAnyError(operationTag: String, errorMessage: String, type: ErrorType?) {
        showMsg.set(errorMessage)
    }

    private fun errorMsg(credential: UserCredential): String {
        return when {
            credential.emailId.isEmpty()->{
                emptyEmailField
            }
            !credential.isEmailValid -> {
                invalidEmailMsg
            }
            credential.password.isEmpty() ->{
                emptyPasswordField
            }
            !credential.isPasswordValid -> {
                invalidPasswordMsg
            }
            else -> {
                undefinedErrorMsg
            }
        }
    }

    private companion object {
        const val emailLogin = "email_login"
    }
}