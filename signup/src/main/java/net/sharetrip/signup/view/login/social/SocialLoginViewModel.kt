package net.sharetrip.signup.view.login.social

import androidx.databinding.ObservableBoolean
import androidx.lifecycle.MutableLiveData
import com.facebook.login.LoginManager
import com.google.android.gms.auth.api.signin.GoogleSignInAccount
import com.sharetrip.base.data.PrefKey
import com.sharetrip.base.data.PrefKey.ACCESS_TOKEN
import com.sharetrip.base.data.SharedPrefsHelper
import com.sharetrip.base.event.Event
import net.sharetrip.shared.model.UserInfo
import com.sharetrip.base.network.model.BaseResponse
import com.sharetrip.base.network.model.ErrorType
import com.sharetrip.base.network.model.RestResponse
import com.sharetrip.base.viewmodel.BaseOperationalViewModel
import com.squareup.moshi.Moshi
import net.sharetrip.signup.model.AppleToken
import net.sharetrip.signup.network.SingUpApiService
import net.sharetrip.signup.utils.UtilText.SUCCESS
import javax.inject.Inject
import javax.inject.Named

class SocialLoginViewModel(
    private val apiService: SingUpApiService,
    private var sharedPrefsHelper: SharedPrefsHelper

) : BaseOperationalViewModel() {

    val errorText = MutableLiveData<String>()
    val isLoading = ObservableBoolean(false)
    var goToEmailLogin = MutableLiveData<Event<Boolean>>()
    var goToSignup = MutableLiveData<Event<Boolean>>()
    var skipClicked = MutableLiveData<Event<Boolean>>()
    val setActivityResult = MutableLiveData<Boolean>()

    @JvmField
    @field:[Inject Named("result")]
    var isActivityResult: Boolean = false
    var requestCode: Int = 0

    fun onClickEmailLogin() {
        goToEmailLogin.value = Event(true)
    }

    fun onClickSignUp() {
        goToSignup.value = Event(true)
    }

    fun onClickSkip() {
        skipClicked.value = Event(true)
    }

    fun loginWithFacebook(token: String) {
        executeSuspendedCodeBlock(
            operationTag = facebookLogin,
            codeBlock = { apiService.getFacebookLoginResponse(token) }
        )
    }

    fun loginWithGoogle(account: GoogleSignInAccount) {
        executeSuspendedCodeBlock(
            operationTag = googleLogin,
            codeBlock = { apiService.getGoogleLoginResponse(account.idToken.toString()) }
        )
    }

    fun loginWithApple(token: String) {
        isLoading.set(true)
        executeSuspendedCodeBlock(
            operationTag = appleLogin,
            codeBlock = { apiService.signInWithApple(AppleToken(token)) }
        )
    }

    override fun onSuccessResponse(operationTag: String, data: BaseResponse.Success<Any>) {
        when (operationTag) {
            facebookLogin -> {
                val response = data.body as RestResponse<UserInfo>
                if (response.code == SUCCESS) {
                    updateUserInfoInSharedPref(response)
                    setActivityResult.value = true
                }
            }

            googleLogin -> {
                val response = data.body as RestResponse<UserInfo>
                if (response.code == SUCCESS) {
                    updateUserInfoInSharedPref(response)
                    setActivityResult.value = true
                }
            }

            appleLogin -> {
                isLoading.set(false)
                val response = data.body as RestResponse<UserInfo>
                if (response.code == SUCCESS) {
                    updateUserInfoInSharedPref(response)
                    setActivityResult.value = true
                }
            }
        }
    }

    override fun onAnyError(operationTag: String, errorMessage: String, type: ErrorType?) {
        if (operationTag == facebookLogin) {
            LoginManager.getInstance().logOut()
        }

        isLoading.set(false)
        errorText.value = errorMessage
    }

    private fun updateUserInfoInSharedPref(response: RestResponse<UserInfo>, account: GoogleSignInAccount? = null) {
        val moshi = Moshi.Builder().build()
        val jsonAdapter = moshi.adapter(UserInfo::class.java)
        sharedPrefsHelper.put(ACCESS_TOKEN, response.response.token)

        if (account != null) {
            if (account.displayName != null) {
                try {
                    response.response.firstName = account.displayName!!.split(" ")[0]
                    response.response.lastName = account.displayName!!.split(" ")[1]
                } catch (e: Exception) {
                    response.response.firstName = account.displayName!!
                }
            }
            if (account.email != null)
                response.response.email = account.email!!
            if (account.photoUrl != null)
                response.response.avatar = account.photoUrl.toString()
        }

        val userInfo = jsonAdapter.toJson(response.response)
        sharedPrefsHelper.put(PrefKey.USER_INFO, userInfo)
        sharedPrefsHelper.put(PrefKey.USER_AVATER, response.response.avatar)
        sharedPrefsHelper.put(PrefKey.IS_LOGIN, true)
    }

    private companion object {
        const val facebookLogin = "facebook_login"
        const val googleLogin = "google_login"
        const val appleLogin = "apple_login"
    }

}