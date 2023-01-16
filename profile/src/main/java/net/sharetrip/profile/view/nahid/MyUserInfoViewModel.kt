package net.sharetrip.profile.view.nahid
import androidx.databinding.ObservableField
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.viewModelScope
import com.sharetrip.base.data.PrefKey
import com.sharetrip.base.data.SharedPrefsHelper
import com.sharetrip.base.event.Event
import com.sharetrip.base.network.model.Status
import net.sharetrip.shared.utils.analytics.AnalyticsProvider
import com.sharetrip.base.viewmodel.BaseViewModel
import kotlinx.coroutines.launch
import net.sharetrip.profile.model.UserProfile

class MyUserInfoViewModel(
    private val sharedPrefsHelper: SharedPrefsHelper,
    private val repository: MyUserInfoRepository
) : BaseViewModel() {

    val apiStatus = repository.apiStatus

    var userInfo = ObservableField<UserProfile>()
    var loadImage = MutableLiveData<String>()
    var imageUploadChosenNew = MutableLiveData<Event<String>>()
    var imageUploadChosenOld = MutableLiveData<Event<String>>()
    var requestCode: Int = 0
    private val profileEventManager =
        AnalyticsProvider.profileEventManager(AnalyticsProvider.getFirebaseAnalytics())

    init {
        userInfo.set(UserProfile())
        fetchUserProfile()
    }

    private fun fetchUserProfile() {
        val token = sharedPrefsHelper[PrefKey.ACCESS_TOKEN, ""]

        viewModelScope.launch {
            repository.getProfileInfoFromRepo(token)

            if (apiStatus.value == Status.SUCCESS) {
                val profile = repository.myProfile.value
                if (profile?.avatar?.isNotEmpty()!!) {
                    loadImage.value = profile.avatar
                }
                userInfo.set(profile)
            }
        }
    }

    fun onclickSaveInfo() {
        updateMyProfile()
    }

    private fun updateMyProfile() {
        val userInfo = userInfo.get()
        userInfo?.let {
            val token = sharedPrefsHelper[PrefKey.ACCESS_TOKEN, ""]

            viewModelScope.launch {
                repository.updateProfileInfoFromRepo(token, userInfo)

                if (apiStatus.value == Status.SUCCESS) {
                    showMessage(PROFILE_UPDATED)
                    fetchUserProfile()
                } else {
                    showMessage(PROFILE_NOT_UPDATED)
                }
            }
        }
    }


    fun onClickProfilePic() {
        profileEventManager.uploadPhotoInProfile()
        if (userInfo.get()?.avatar != "") {
            imageUploadChosenOld.value = Event("profile")
        } else {
            imageUploadChosenNew.value = Event("profile")
        }
    }


    companion object {
        const val SUCCESS = "SUCCESS"
        const val PROFILE_NOT_UPDATED = "User Profile not updated"
        const val PROFILE_UPDATED = "User Profile updated"

    }
}
