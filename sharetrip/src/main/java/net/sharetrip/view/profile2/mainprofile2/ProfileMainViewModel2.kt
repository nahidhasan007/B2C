package net.sharetrip.view.profile.mainprofile

import androidx.databinding.ObservableBoolean
import androidx.databinding.ObservableField
import androidx.lifecycle.MutableLiveData
import com.facebook.login.LoginManager
import com.sharetrip.base.data.PrefKey
import com.sharetrip.base.data.SharedPrefsHelper
import com.sharetrip.base.event.Event
import net.sharetrip.shared.model.GuestPopUpData
import net.sharetrip.shared.utils.analytics.AnalyticsProvider
import com.sharetrip.base.viewmodel.BaseViewModel
import net.sharetrip.BuildConfig
import net.sharetrip.R
import net.sharetrip.profile.model.ProfileAction

class ProfileMainViewModel2(val sharedPrefsHelper: SharedPrefsHelper) : BaseViewModel() {

    val isLogin = ObservableBoolean()
    var versionName = ObservableField<String>()
    val sendMail = MutableLiveData<Boolean>()
    private val avatarUrl = ObservableField<Any>()
    val goToProfileDetails = MutableLiveData<Event<Boolean>>()
    val goToLogin = MutableLiveData<Event<Boolean>>()
    lateinit var profileAction: String

    val popupData: GuestPopUpData by lazy {
        GuestPopUpData(
            R.string.common_title,
            R.string.flight_body,
            R.drawable.ic_flight_blue, null
        )
    }

    private val profileEventManager = AnalyticsProvider.profileEventManager(AnalyticsProvider.getFirebaseAnalytics())

    init {
        versionName.set("Version: " + BuildConfig.VERSION_NAME)
    }

    fun checkLoginInformation() {
        isLogin.set(sharedPrefsHelper[PrefKey.IS_LOGIN, false])
        avatarUrl.set(if (isLogin.get()) sharedPrefsHelper[PrefKey.USER_AVATER, ""] else R.drawable.ic_avater_profile)
    }

    fun onClickLogin() {
        goToLogin.value = Event(true)
    }

    fun onClickEditProfile() {
        profileEventManager.clickEditProfile()
        profileAction = ProfileAction.ARG_PROFILE_EDIT
        goToProfileDetails.value = Event(true)
    }

    fun onClickLogOut() {
        LoginManager.getInstance().logOut()
        sharedPrefsHelper.put(PrefKey.ACCESS_TOKEN, "")
        sharedPrefsHelper.put(PrefKey.IS_LOGIN, false)
        sharedPrefsHelper.put(PrefKey.USER_TRIP_COIN, "0")
        sharedPrefsHelper.put(PrefKey.QUIZ_NEXT_DAY, 0L)
        sharedPrefsHelper.put(PrefKey.FREE_TRIP_COIN_TIME,0)
        goToLogin.value = Event(true)
    }

}

