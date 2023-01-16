package net.sharetrip.profile.view.user

import android.view.View
import androidx.databinding.ObservableBoolean
import androidx.databinding.ObservableField
import androidx.lifecycle.LiveData
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
import net.sharetrip.shared.utils.*
import okhttp3.MediaType
import okhttp3.MediaType.Companion.toMediaTypeOrNull
import okhttp3.MultipartBody
import okhttp3.RequestBody
import okhttp3.RequestBody.Companion.asRequestBody
import java.io.File

class UserInfoViewModel(
    private val sharedPrefsHelper: SharedPrefsHelper,
    private val repository: UserInfoRepository
) : BaseViewModel() {

    private val _navigateToCountryCurrency = MutableLiveData<Event<Boolean>>()
    val navigateToCountryCurrency: LiveData<Event<Boolean>>
        get() = _navigateToCountryCurrency

    private val _navigateToNameGuideLine = MutableLiveData<Event<Boolean>>()
    val navigateToNameGuideLine: LiveData<Event<Boolean>>
        get() = _navigateToNameGuideLine

    val apiStatus = repository.apiStatus

    var userInfo = ObservableField<UserProfile>()
    var isMaleSelected = ObservableBoolean()
    var isExpandView = ObservableBoolean()
    var isLoaderShow = repository.isDataLoading
    var passportProgress = MutableLiveData<Int>()
    var isFirstNameValid = MutableLiveData<Boolean>()
    var isLastNameValid = MutableLiveData<Boolean>()
    var loadImage = MutableLiveData<String>()
    var imageUploadChosenNew = MutableLiveData<Event<String>>()
    var imageUploadChosenOld = MutableLiveData<Event<String>>()
    var userBirthDate = MutableLiveData<String>()
    var requestCode: Int = 0
    var isPassportNumberValid = MutableLiveData<Boolean>()
    var isPassportExpiryDateValid = MutableLiveData<Boolean>()
    private val profileEventManager =
        AnalyticsProvider.profileEventManager(AnalyticsProvider.getFirebaseAnalytics())

    init {
        isMaleSelected.set(true)
        isExpandView.set(false)
        userInfo.set(UserProfile())
        fetchUserProfile()
    }

    private fun fetchUserProfile() {
        val token = sharedPrefsHelper[PrefKey.ACCESS_TOKEN, ""]

        viewModelScope.launch {
            repository.getProfileInfoFromRepo(token)

            if (apiStatus.value == Status.SUCCESS) {
                val profile = repository.userProfile.value
                if (profile?.avatar?.isNotEmpty()!!) {
                    loadImage.value = profile.avatar
                }
                profile.apply {
                    if (nationality == "") {
                        nationality = "BD"
                        country = "Bangladesh"
                    }
                    userBirthDate.value = dateOfBirth
                    dateOfBirth = dateOfBirth.dateChangeToDDMMYYYY()
                    passportExpireDate = passportExpireDate.dateChangeToDDMMYYYY()
                }
                if (profile.passportCopy.isNotEmpty()) {
                    passportProgress.value = 100
                }
                if (profile.gender == "Female") {
                    isMaleSelected.set(false)
                }
                userInfo.set(profile)
            }
        }
    }

    fun onClickSaveMenu() {
        sendUpdateProfile()
    }

    private fun sendUpdateProfile() {
        val userInfo = userInfo.get()
        userInfo?.let {
            if (userInfo.surName.isNameValid()) {
                val token = sharedPrefsHelper[PrefKey.ACCESS_TOKEN, ""]

                if (!userInfo.givenName.isGivenNameValid()) {
                    showMessage(INVALID_NAME_FORMAT)
                    return
                }


                if (userInfo.mobileNumber.isEmpty()) {
                    showMessage(EMPTY_MOBILE)
                    return
                } else if (!userInfo.mobileNumber.matches(PHONE_VALIDATION_REGEX.toRegex())) {
                    showMessage(ENTER_VALID_PHONE)
                    return
                }

                if (isMaleSelected.get()) {
                    userInfo.gender = "Male"
                } else {
                    userInfo.gender = "Female"
                }

                if (!userInfo.isValidPassportOrEmpty()) {
                    showMessage(INVALID_PASSPORT)
                    return
                }

                if (userInfo.passportNumber.isNotEmpty() && userInfo.passportExpireDate.isEmpty()) {
                    showMessage(ENTER_PASSPORT_EXPIRY_DATE)
                    return
                }

                if (userInfo.passportExpireDate.isNotEmpty() && userInfo.passportNumber.isEmpty()) {
                    showMessage(ENTER_PASSPORT_NUMBER)
                    return
                }

                try {
                    userInfo.dateOfBirth =
                        DateUtil.parseApiDateFormatFromDisplayCommonDateFormat(userInfo.dateOfBirth)
                    userInfo.passportExpireDate =
                        DateUtil.parseApiDateFormatFromDisplayCommonDateFormat(userInfo.passportExpireDate)
                    userInfo.titleName =
                        userInfo.dateOfBirth.let { userInfo.gender.getUserTitle(it) }

                } catch (e: Exception) {
                    showMessage(PARSE_ERROR)
                    return
                }

                if (!userInfo.isUserAgeValid()) {
                    showMessage(INVALID_BIRTHDATE)
                    return
                }

                viewModelScope.launch {
                    repository.updateProfileInfoFromRepo(token, userInfo)

                    if (apiStatus.value == Status.SUCCESS) {
                        showMessage(PROFILE_UPDATED)
                        sharedPrefsHelper.put(
                            PrefKey.USER_AVATER,
                            repository.userProfile.value?.avatar
                        )
                        fetchUserProfile()
                    } else {
                        showMessage(PROFILE_NOT_UPDATED)
                    }
                }
            } else {
                showMessage(INVALID_NAME_FORMAT)
            }
        }
    }

    fun updateImageFile(photoFile: File, tag: String, mime: String) {
        val token = sharedPrefsHelper[PrefKey.ACCESS_TOKEN, ""]
        val requestPhotoFile = photoFile.asRequestBody(mime.toMediaTypeOrNull())
        val userPhoto =
            MultipartBody.Part.createFormData("uploadFile", photoFile.name, requestPhotoFile)
        if (tag == "passport") {
            passportProgress.value = 50
        }

        viewModelScope.launch {
            if (tag == "profile") {
                repository.uploadAvatarPhotoToServer(token, userPhoto)
            } else {
                repository.uploadFileToServer(token, userPhoto)
            }

            if (apiStatus.value == Status.SUCCESS) {
                when (tag) {
                    "profile" -> {
                        userInfo.get()?.avatar = repository.imageUploadResponse.value?.path ?: ""
                        showMessage(PROFILE_PICTURE_UPDATED)
                    }
                    "passport" -> {
                        userInfo.get()!!.passportCopy = repository.imageUploadResponse.value?.path!!
                        passportProgress.value = 100
                        showMessage(PASSPORT_UPLOADED)
                    }
                }
            }
        }
    }

    fun onClickNationality() {
        _navigateToCountryCurrency.value = Event(true)
    }

    fun setCountryCode(code: String, name: String) {
        val profile = userInfo.get()
        profile!!.nationality = code
        profile.country = name
        userInfo.set(profile)
    }

    fun onClickPassportCopy() {
        profileEventManager.uploadPassportInProfile()

        if (passportProgress.value == 100) {
            imageUploadChosenOld.value = Event("passport")
        } else {
            imageUploadChosenNew.value = Event("passport")
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

    fun onClickFemale(view: View) {
        isMaleSelected.set(false)
    }

    fun onClickMale(view: View) {
        isMaleSelected.set(true)
    }

    fun onClickMore() {
        profileEventManager.clickProfileMoreInfo()
        isExpandView.set(true)
    }

    fun onClickLess(view: View) {
        isExpandView.set(false)
    }

    fun onTextChangedForFirstName(s: CharSequence, start: Int, before: Int, count: Int) {
        isFirstNameValid.value = if (s.toString().isEmpty()) true else s.toString().isNameValid()
    }

    fun onTextChangedForLastName(s: CharSequence, start: Int, before: Int, count: Int) {
        isLastNameValid.value = s.toString().isNameValid()
    }

    fun onTextChangedForPassportNumber(s: CharSequence, start: Int, before: Int, count: Int) {
        isPassportNumberValid.value = s.toString().isPassportNumberValid()
    }

    fun onTextChangedForPassportExpiryDate(s: CharSequence, start: Int, before: Int, count: Int) {
        isPassportExpiryDateValid.value = s.toString().isPassportExpiryDateValid()
    }

    fun openNameGuildLine() {
        _navigateToNameGuideLine.value = Event(true)
    }

    companion object {
        const val SUCCESS = "SUCCESS"
        const val PROFILE_PICTURE_UPDATED = "Profile Picture successfully updated"
        const val PASSPORT_UPLOADED = "Uploaded!! Now Save the data"
        const val PROFILE_NOT_UPDATED = "User Profile not updated"
        const val PROFILE_UPDATED = "User Profile updated"
        const val INVALID_BIRTHDATE =
            "The information provided is not valid, kindly provide the valid date of birth"
        const val PARSE_ERROR = "An error happen to parse"
        const val ENTER_PASSPORT_NUMBER = "Please enter passport number"
        const val ENTER_PASSPORT_EXPIRY_DATE = "Please enter passport expiry date"
        const val INVALID_PASSPORT = "Invalid passport number"
        const val ENTER_VALID_PHONE =
            "Please provide a valid phone number with proper country code."
        const val EMPTY_MOBILE = "Mobile number is empty!"
        const val INVALID_NAME_FORMAT = "Invalid Name Format"
    }
}
