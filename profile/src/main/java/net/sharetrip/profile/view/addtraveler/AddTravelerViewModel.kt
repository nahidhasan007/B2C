package net.sharetrip.profile.view.addtraveler

import android.text.TextUtils
import android.view.View
import androidx.databinding.ObservableBoolean
import androidx.databinding.ObservableField
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.viewModelScope
import com.sharetrip.base.data.PrefKey
import com.sharetrip.base.data.SharedPrefsHelper
import com.sharetrip.base.event.Event
import com.sharetrip.base.network.model.Status
import com.sharetrip.base.viewmodel.BaseViewModel
import kotlinx.coroutines.launch
import net.sharetrip.profile.model.Traveler
import net.sharetrip.profile.view.user.UserInfoViewModel.Companion.INVALID_PASSPORT
import net.sharetrip.shared.utils.*
import okhttp3.MediaType
import okhttp3.MediaType.Companion.toMediaTypeOrNull
import okhttp3.MultipartBody
import okhttp3.RequestBody
import okhttp3.RequestBody.Companion.asRequestBody
import java.io.File

class AddTravelerViewModel(
    private val sharedPrefsHelper: SharedPrefsHelper,
    private val repository: AddTravelerRepository
) : BaseViewModel() {
    val traveler = ObservableField<Traveler>()
    val country = ObservableField<String>()
    var isMaleSelected = ObservableBoolean()
    val isDataLoading = repository.isDataLoading
    val apiStatus = repository.apiStatus
    val imageUploadResponse = repository.imageUploadResponse

    var imageUploadChosserNew = MutableLiveData<Event<String>>()
    var imageUploadChosserOld = MutableLiveData<Event<String>>()
    var passportProgress = MutableLiveData<Int>()
    var visaProgress = MutableLiveData<Int>()
    var isLoaderShow = MutableLiveData<Boolean>()
    var isFirstNameValid = MutableLiveData<Boolean>()
    var isLastNameValid = MutableLiveData<Boolean>()
    var showToastMessage = repository.showMessage

    var isPassportNumberValid = MutableLiveData<Boolean>()
    var isPassportExpiryDateValid = MutableLiveData<Boolean>()
    var requestCode: Int = 0

    init {
        isMaleSelected.set(true)
        val traveler = Traveler()
        traveler.nationality = "BD"
        country.set("Bangladesh")
        this.traveler.set(traveler)
    }

    fun onClickSaveMenu() {
        val travelerInfo = traveler.get()
        if (travelerInfo == null) {
            showMessage("Some thing wrong, Please try again")
            return
        }
        if (!travelerInfo.isTravelerAgeValid()) {
            showMessage(
                "The information provided is not valid, kindly provide the valid date of birth"
            )
            return
        }

        if (!travelerInfo.givenName.isGivenNameValid()) {
            showMessage("Invalid Name Format")
            return
        }

        if (!isInputValid(travelerInfo)) {
            showToastMessage.value = Event("Please fill up all the required fields.")
            return
        }

        if (!travelerInfo.isValidPassport()) {
            isLoaderShow.value = false
            showMessage(INVALID_PASSPORT)
            return
        }

        travelerInfo.apply {
            dateOfBirth = DateUtil.parseApiDateFormatFromDisplayCommonDateFormat(dateOfBirth)
            passportExpireDate =
                DateUtil.parseApiDateFormatFromDisplayCommonDateFormat(passportExpireDate)
            gender = if (isMaleSelected.get()) {
                "Male"
            } else {
                "Female"
            }
            titleName = gender.getUserTitle(dateOfBirth)
        }
        addNewTraveler(travelerInfo)
    }

    private fun addNewTraveler(traveler: Traveler) {
        val token = sharedPrefsHelper[PrefKey.ACCESS_TOKEN, ""]

        viewModelScope.launch {
            repository.addTraveller(token, traveler)

            if (apiStatus.value == Status.SUCCESS) {
                navigateWithArgument(GOTO_BACK, "")
            }
        }
    }

    fun onClickNationality() {
        navigateWithArgument(GOTO_COUNTRY_CURRENCY, "")
    }

    fun setCountryCode(code: String, name: String) {
        traveler.get()?.nationality = code
        country.set(name)
    }

    private fun isInputValid(traveler: Traveler): Boolean {
        return (!TextUtils.isEmpty(traveler.surName)
                && !TextUtils.isEmpty(traveler.dateOfBirth)
                && !TextUtils.isEmpty(traveler.nationality)
                && !TextUtils.isEmpty(traveler.passportNumber)
                && !TextUtils.isEmpty(traveler.passportExpireDate)
                && traveler.surName.isNameValid())
    }

    fun onClickPassportCopy(view: View) {
        if (passportProgress.value == 100) {
            imageUploadChosserOld.value = Event("passport")
        } else {
            imageUploadChosserNew.value = Event("passport")
        }
    }

    fun onClickVisaCopy(view: View) {
        if (visaProgress.value == 100) {
            imageUploadChosserOld.value = Event("visa")
        } else {
            imageUploadChosserNew.value = Event("visa")
        }
    }

    fun updateImageFile(photoFile: File, tag: String, mime: String) {
        val token = sharedPrefsHelper[PrefKey.ACCESS_TOKEN, ""]
        val requestPhotoFile = photoFile.asRequestBody(mime.toMediaTypeOrNull())
        val userPhoto =
            MultipartBody.Part.createFormData("uploadFile", photoFile.name, requestPhotoFile)

        if (tag == "passport") {
            passportProgress.value = 50
        } else if (tag == "visa") {
            visaProgress.value = 50
        }

        viewModelScope.launch {
            repository.uploadImage(token, userPhoto)

            if (apiStatus.value == Status.SUCCESS) {
                when (tag) {
                    "passport" -> {
                        traveler.get()!!.passportCopy = imageUploadResponse.value?.path!!
                        passportProgress.value = 100
                        showToastMessage.value = Event("Uploaded!! Now Save the data")
                    }
                    "visa" -> {
                        traveler.get()!!.visaCopy = imageUploadResponse.value?.path!!
                        visaProgress.value = 100
                        showToastMessage.value = Event("Uploaded!! Now Save the data")

                    }
                }
            }
        }
    }

    fun gotoImagePreview(tag: String) {
        navigateWithArgument(GOTO_IMAGE_PREVIEW, tag)
    }

    fun onClickFemale(view: View) {
        isMaleSelected.set(false)
    }

    fun onClickMale(view: View) {
        isMaleSelected.set(true)
    }

    fun onTextChangedForFirstName(s: CharSequence, start: Int, before: Int, count: Int) {
        isFirstNameValid.value = s.toString().isGivenNameValid()
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

    companion object {
        const val GOTO_COUNTRY_CURRENCY = "GOTO_COUNTRY_CURRENCY"
        const val GOTO_IMAGE_PREVIEW = "GOTO_IMAGE_PREVIEW"
        const val GOTO_BACK = "GOTO_BACK"
    }
}
