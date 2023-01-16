package net.sharetrip.view.dashboard

import android.view.View
import androidx.lifecycle.MutableLiveData
import com.sharetrip.base.data.PrefKey
import com.sharetrip.base.data.SharedPrefsHelper
import com.sharetrip.base.network.model.BaseResponse
import com.sharetrip.base.network.model.ErrorType
import com.sharetrip.base.network.model.RestResponse
import com.sharetrip.base.viewmodel.BaseOperationalViewModel
import net.sharetrip.model.DashboardActivityApiCallingKey
import net.sharetrip.model.FcmTokenModel
import net.sharetrip.network.MainApiService
import net.sharetrip.shared.model.user.User
import net.sharetrip.shared.utils.analytics.AnalyticsProvider
import net.sharetrip.utils.SingleLiveEvent

class DashboardActivityViewModel(
    private val apiService: MainApiService,
    private val sharedPrefsHelper: SharedPrefsHelper
) :
    BaseOperationalViewModel() {
    val navigateToOnBoarding = SingleLiveEvent<Any>()
    val navigateToProfile = SingleLiveEvent<Any>()
    val navigateToRegistration = SingleLiveEvent<Any>()
    val guestUserInfo = SingleLiveEvent<Triple<String, Int, String>>()
    val userInfo = MutableLiveData<User>()

    val homePageEventManager = AnalyticsProvider.homePageEventManager(AnalyticsProvider.getFirebaseAnalytics())

    override fun onSuccessResponse(operationTag: String, data: BaseResponse.Success<Any>) {
        when (operationTag) {
            DashboardActivityApiCallingKey.FirebaseToken.name -> {
                val fcmResponse = (data.body as RestResponse<*>).response as FcmTokenModel
                sharedPrefsHelper.put(
                    PrefKey.LAST_FIREBASE_TOKEN_IN_SERVER,
                    fcmResponse.token
                )
            }

            DashboardActivityApiCallingKey.UserInformation.name -> {
                val response = (data.body as RestResponse<*>).response as User
                updateProfileInformation(response)
                updateFirebaseToken(response.username)
                userInfo.value = response
            }
        }
    }

    override fun onAnyError(operationTag: String, errorMessage: String, type: ErrorType?) {
        when (operationTag) {
            DashboardActivityApiCallingKey.FirebaseToken.name -> {
                sharedPrefsHelper.put(PrefKey.LAST_FIREBASE_TOKEN_IN_SERVER, "")
            }

            DashboardActivityApiCallingKey.UserInformation.name -> {
                when (type) {
                    ErrorType.API_ERROR, ErrorType.UNKNOWN_ERROR -> {
                        updateProfileInformation(null)
                        guestUserInfo.value = Triple("No Name", View.GONE, "0")
                        sharedPrefsHelper.put(PrefKey.USER_TRIP_COIN, "0")
                        navigateToRegistration.call()
                    }
                    else -> {}
                }
            }
        }
    }

    fun updateUserStatus() {
        if (sharedPrefsHelper[PrefKey.IS_LOGIN, false]) {
            updateUserInfo()
        } else {
            updateGuestInfo()
        }
    }

    private fun updateGuestInfo() {
        guestUserInfo.value = Triple("No Name", View.GONE, "0")
    }

    private fun updateUserInfo() {
        val token = sharedPrefsHelper[PrefKey.ACCESS_TOKEN, ""]
        if (token == "") return

        executeSuspendedCodeBlock(DashboardActivityApiCallingKey.UserInformation.name) {
            apiService.getUserInformation(
                token
            )
        }
    }

    private fun updateProfileInformation(user: User?) {
        if (user != null) {
            if (user.lastName.isEmpty()) {
                sharedPrefsHelper.put(PrefKey.USER_NAME, "No Name")
            } else {
                sharedPrefsHelper.put(PrefKey.USER_NAME, user.firstName)
            }
            sharedPrefsHelper.put(PrefKey.USER_LAST_NAME, user.lastName)
            sharedPrefsHelper.put(PrefKey.USER_FULL_NAME, user.firstName + " " + user.lastName)
            sharedPrefsHelper.put(PrefKey.USER_AVATER, user.avatar)
            sharedPrefsHelper.put(PrefKey.USER_LOYALTY_TYPE, user.userLevel)
            sharedPrefsHelper.put(PrefKey.USER_TRIP_COIN, user.totalPoints.toString())
            sharedPrefsHelper.put(PrefKey.USER_EMAIL, user.email)
            sharedPrefsHelper.put(PrefKey.USER_REFERRAL_CODE, user.referralCode)
            sharedPrefsHelper.put(
                PrefKey.USER_REFERRAL_COIN,
                user.coinSettings.referCoin.toString()
            )
            sharedPrefsHelper.put(PrefKey.TREASURE_BOX_COIN, user.coinSettings.treasureBoxCoin)
        } else {
            sharedPrefsHelper.put(PrefKey.ACCESS_TOKEN, "")
            sharedPrefsHelper.put(PrefKey.IS_LOGIN, false)
            sharedPrefsHelper.put(PrefKey.USER_TRIP_COIN, "0")
        }
    }

    private fun updateFirebaseToken(userID: String) {
        val currentFirebaseToken = sharedPrefsHelper[PrefKey.FIREBASE_DEVICE_TOKEN, ""]
        val lastServerFirebaseToken = sharedPrefsHelper[PrefKey.LAST_FIREBASE_TOKEN_IN_SERVER, ""]
        if (currentFirebaseToken == lastServerFirebaseToken) {
            return
        }
        val email = sharedPrefsHelper[PrefKey.USER_EMAIL, ""]
        val fullName = sharedPrefsHelper[PrefKey.USER_FULL_NAME, ""]
        val fcmTokenModel = FcmTokenModel(userID, email, currentFirebaseToken, fullName)

        executeSuspendedCodeBlock(DashboardActivityApiCallingKey.FirebaseToken.name) {
            apiService.uploadFirebaseToken(
                fcmTokenModel
            )
        }
    }

    fun checkBoardingStatus(valueFromNotification: Int, notificationData: String?) {
        if (!sharedPrefsHelper[PrefKey.IS_ON_BOARDING_ONCE, false]) {
            navigateToOnBoarding.call()
        } else {
            if (valueFromNotification == 1) {
                sharedPrefsHelper.put(PrefKey.NOTIFICATION_DETAIL, notificationData)
                homePageEventManager.clickOnOpenNotification()
                navigateToProfile.call()
            }
        }
    }
}
