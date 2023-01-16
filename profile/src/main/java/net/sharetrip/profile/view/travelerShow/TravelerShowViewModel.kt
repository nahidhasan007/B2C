package net.sharetrip.profile.view.travelerShow

import android.text.TextUtils
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
import com.sharetrip.base.viewmodel.BaseViewModel
import kotlinx.coroutines.launch
import net.sharetrip.profile.model.RemoveTravelerInfo
import net.sharetrip.profile.model.Traveler
import net.sharetrip.profile.view.user.UserInfoViewModel.Companion.INVALID_PASSPORT
import net.sharetrip.shared.utils.*
import okhttp3.MediaType
import okhttp3.MediaType.Companion.toMediaTypeOrNull
import okhttp3.MultipartBody
import okhttp3.RequestBody
import okhttp3.RequestBody.Companion.asRequestBody
import java.io.File

class TravelerShowViewModel(
    passengerInfo: Traveler, private val sharedPrefsHelper: SharedPrefsHelper,
    private val travelerShowRepo: TravelerShowRepo
) : BaseViewModel() {

    private val _navigateUp = MutableLiveData<Event<Boolean>>()
    val navigateTup: LiveData<Event<Boolean>>
        get() = _navigateUp

    private val _navigateToImagePreview = MutableLiveData<Event<Pair<String,Boolean>>>()
    val navigateToImagePreview: LiveData<Event<Pair<String,Boolean>>>
        get() = _navigateToImagePreview

    private val _navigateToCountryCurrency = MutableLiveData<Event<Boolean>>()
    val navigateToCountryCurrency: LiveData<Event<Boolean>>
    get() = _navigateToCountryCurrency

    val passenger = ObservableField<Traveler>()
    val country = ObservableField<String>()
    val isDataLoading = travelerShowRepo.isDataLoading
    val apiStatus = travelerShowRepo.apiStatus
    val imageUploadResponse = travelerShowRepo.imageUploadResponse

    var isMaleSelected = ObservableBoolean()
    var imageUploadChooserNew = MutableLiveData<Event<String>>()
    var imageUploadChooserOld = MutableLiveData<Event<String>>()
    var passportProgress = MutableLiveData<Int>()
    var visaProgress = MutableLiveData<Int>()
    var isFirstNameValid = MutableLiveData<Boolean>()
    var isLastNameValid = MutableLiveData<Boolean>()
    var userBirthdate = MutableLiveData<String>()
    var showToastMessage = travelerShowRepo.showMessage
    var requestCode: Int = 0
    var isPassportNumberValid = MutableLiveData<Boolean>()
    var isPassportExpiryDateValid = MutableLiveData<Boolean>()

    init {
        userBirthdate.value = passengerInfo.dateOfBirth
        isMaleSelected.set(true)
        passengerInfo.dateOfBirth = passengerInfo.dateOfBirth.dateChangeToDDMMYYYY()
        passengerInfo.passportExpireDate = passengerInfo.passportExpireDate.dateChangeToDDMMYYYY()
        if (passengerInfo.passportCopy != "") {
            passportProgress.value = 100
        }
        if (passengerInfo.visaCopy != "") {
            visaProgress.value = 100
        }
        if (passengerInfo.gender == "Female") {
            isMaleSelected.set(false)
        }
        country.set(passengerInfo.nationality)
        passenger.set(passengerInfo)
    }

    fun onClickSaveMenu() {
        val token = sharedPrefsHelper[PrefKey.ACCESS_TOKEN, ""]

        if (!isInputValid()) {
            showToastMessage.value = Event("Please fill up all the required fields.")
            return
        }

        val traveler = passenger.get()
        traveler?.let {
            if (!traveler.isValidPassport()) {
                showToastMessage.value = Event(INVALID_PASSPORT)
                return
            }
        }

        if (DateUtil.isDateIsValid(passenger.get()?.dateOfBirth)) {
            passenger.get()?.dateOfBirth =
                DateUtil.parseApiDateFormatFromDisplayCommonDateFormat(passenger.get()?.dateOfBirth)
        }

        if (DateUtil.isDateIsValid(passenger.get()?.passportExpireDate)) {
            passenger.get()?.passportExpireDate =
                DateUtil.parseApiDateFormatFromDisplayCommonDateFormat(passenger.get()?.passportExpireDate)
        }

        passenger.get()?.gender = if (isMaleSelected.get()) {
            "Male"
        } else {
            "Female"
        }

        passenger.get()?.titleName =
            passenger.get()?.dateOfBirth.let { passenger.get()?.gender.getUserTitle(it!!) }
                .toString()

        viewModelScope.launch {
            travelerShowRepo.updateTravelerInfo(token, passenger.get()!!)

            if (apiStatus.value == Status.SUCCESS) {
                showToastMessage.value = Event("Traveler Info Updated!")
                _navigateUp.value = Event(true)
            }
        }
    }

    fun onClickRemove() {
        val token = sharedPrefsHelper.get(PrefKey.ACCESS_TOKEN, "")
        val removeTravelerInfo = RemoveTravelerInfo()
        removeTravelerInfo.isQuickPick = false
        removeTravelerInfo.code = passenger.get()?.code!!

        viewModelScope.launch {
            travelerShowRepo.removeTraveller(token, removeTravelerInfo)

            if (apiStatus.value == Status.SUCCESS) {
                _navigateUp.value = Event(true)
            }
        }
    }

    private fun isInputValid(): Boolean {
        return (!TextUtils.isEmpty(passenger.get()?.surName)
                && !TextUtils.isEmpty(passenger.get()?.dateOfBirth)
                && !TextUtils.isEmpty(passenger.get()?.nationality)
                && !TextUtils.isEmpty(passenger.get()?.passportNumber)
                && !TextUtils.isEmpty(passenger.get()?.passportExpireDate)
                && passenger.get()?.givenName.isGivenNameValid()
                && passenger.get()?.surName.isNameValid())
    }

    fun onClickNationality() {
        _navigateToCountryCurrency.value = Event(true)
    }

    fun setCountryCode(code: String, name: String) {
        passenger.get()?.nationality = code
        country.set(name)
    }

    fun onClickPassportCopy() {
        if (passportProgress.value == 100) {
            imageUploadChooserOld.value = Event("passport")
        } else {
            imageUploadChooserNew.value = Event("passport")
        }
    }

    fun onClickVisaCopy() {
        if (visaProgress.value == 100) {
            imageUploadChooserOld.value = Event("visa")
        } else {
            imageUploadChooserNew.value = Event("visa")
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
            travelerShowRepo.uploadImage(token, userPhoto)

            if (apiStatus.value == Status.SUCCESS) {
                when (tag) {
                    "passport" -> {
                        passenger.get()!!.passportCopy = imageUploadResponse.value?.path!!
                        passportProgress.value = 100
                        showToastMessage.value = Event("Uploaded!! Now Save the data")
                    }
                    "visa" -> {
                        passenger.get()!!.visaCopy = imageUploadResponse.value!!.path
                        visaProgress.value = 100
                        showToastMessage.value = Event("Uploaded!! Now Save the data")
                    }
                }
            }
        }
    }

    fun gotoImagePreview(tag: String) {
        _navigateToImagePreview.value = Event(Pair(tag, true))
    }

    fun onShow() {}

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

}
