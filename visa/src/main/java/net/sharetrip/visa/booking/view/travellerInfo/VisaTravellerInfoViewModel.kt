package net.sharetrip.visa.booking.view.travellerInfo

import android.text.TextUtils
import androidx.databinding.ObservableBoolean
import androidx.databinding.ObservableField
import androidx.lifecycle.MutableLiveData
import com.sharetrip.base.data.PrefKey
import com.sharetrip.base.data.SharedPrefsHelper
import com.sharetrip.base.network.model.BaseResponse
import com.sharetrip.base.network.model.ErrorType
import com.sharetrip.base.network.model.RestResponse
import com.sharetrip.base.viewmodel.BaseOperationalViewModel
import net.sharetrip.shared.utils.*
import net.sharetrip.visa.booking.model.*
import net.sharetrip.visa.network.VisaBookingApiService
import net.sharetrip.visa.utils.MsgUtils.enterAddress
import net.sharetrip.visa.utils.MsgUtils.enterBirthday
import net.sharetrip.visa.utils.MsgUtils.enterEmailAddress
import net.sharetrip.visa.utils.MsgUtils.enterExpiryDate
import net.sharetrip.visa.utils.MsgUtils.enterForeignAddress
import net.sharetrip.visa.utils.MsgUtils.enterGivenName
import net.sharetrip.visa.utils.MsgUtils.enterPassportNumber
import net.sharetrip.visa.utils.MsgUtils.enterSurName
import net.sharetrip.visa.utils.MsgUtils.invalidEmail
import net.sharetrip.visa.utils.MsgUtils.invalidGivenName
import net.sharetrip.visa.utils.MsgUtils.invalidPassport
import net.sharetrip.visa.utils.MsgUtils.invalidPhoneNumber
import net.sharetrip.visa.utils.MsgUtils.selectNationality
import net.sharetrip.visa.utils.MsgUtils.selectProfession
import net.sharetrip.visa.utils.SingleLiveEvent

class VisaTravellerInfoViewModel(
    private val visaSearchQuery: VisaSearchQuery,
    private val apiService: VisaBookingApiService,
    private val sharedPrefsHelper: SharedPrefsHelper
) : BaseOperationalViewModel() {
    private var itemTraveler = VisaItemTraveler()
    val passengerList = MutableLiveData<List<Traveler>>()
    val hideKeyboard = SingleLiveEvent<Any>()
    val navigateToNationality = SingleLiveEvent<Any>()
    val navigateToTravellerVerification = SingleLiveEvent<VisaSearchQuery>()
    val dataInvalidPosition = SingleLiveEvent<Int>()
    val liveValidation = SingleLiveEvent<Pair<Int, Boolean>>()
    var isMaleSelected = ObservableBoolean()
    var isQuickPickEnable = ObservableBoolean()
    var givenName = ObservableField<String>()
    var surName = ObservableField<String>()
    var nationality = ObservableField<String>()
    var passportNumber = ObservableField<String>()
    var passportExpiryDate = ObservableField<String>()
    var currentAddress = ObservableField<String>()
    var foreignAddress = ObservableField<String>()
    var email = ObservableField<String>()
    var phone = ObservableField<String>()
    var birthDate = ObservableField<String>()
    var contactAddress = ObservableField<String>()
    var specialNotes = ObservableField("")
    var isStickerVisa = ObservableBoolean()
    var professionList = MutableLiveData<Pair<String?, List<String>>>()
    private var selectedVisaType = -1
    private var selectedProfessionPosition = -1

    init {
        if (visaSearchQuery.visaType.equals(VisaType.StickerVisa.productName)) {
            isStickerVisa.set(true)
        }

        val traveller = visaSearchQuery.travellers[visaSearchQuery.currentTravellerPosition!!]
        givenName.set(traveller.givenName)
        surName.set(traveller.surName)
        phone.set(traveller.mobileNumber)
        email.set(traveller.email)
        contactAddress.set(traveller.localAddress)

        if (!isStickerVisa.get()) {
            birthDate.set(traveller.dateOfBirth)
            nationality.set(traveller.nationality)
            passportNumber.set(traveller.passportNumber)
            passportExpiryDate.set(traveller.passportExpireDate)
            foreignAddress.set(traveller.foreignAddress)
            currentAddress.set(traveller.localAddress)
            visaSearchQuery.selectedVisaType?.let {
                selectedVisaType = it
            }

            makeProfessionList(traveller.profession)

            if (traveller.gender.isNullOrEmpty() || traveller.gender == "Male") {
                isMaleSelected.set(true)
            } else {
                isMaleSelected.set(false)
            }
        } else {
            specialNotes.set(traveller.specialNotes)
        }

        fetchProfileInfo()
    }

    override fun onSuccessResponse(operationTag: String, data: BaseResponse.Success<Any>) {
        dataLoading.set(false)
        val profileResponse = (data.body as RestResponse<*>).response as UserProfile
        val traveler = Traveler()

        traveler.apply {
            titleName = profileResponse.titleName
            givenName = profileResponse.givenName
            surName = profileResponse.surName
            gender = profileResponse.gender
            nationality = profileResponse.nationality
            dateOfBirth = profileResponse.dateOfBirth
            passportNumber = profileResponse.passportNumber
            passportCopy = profileResponse.passportCopy
            passportExpireDate = profileResponse.passportExpireDate
            visaCopy = profileResponse.visaCopy
            frequentFlyerNumber = profileResponse.frequentFlyerNumber
            mobileNumber = profileResponse.mobileNumber
            email = profileResponse.email
        }

        profileResponse.otherPassengers.add(traveler)
        passengerList.value = profileResponse.otherPassengers as ArrayList<Traveler>
        isQuickPickEnable.set(profileResponse.otherPassengers.isNotEmpty())
    }

    override fun onAnyError(operationTag: String, errorMessage: String, type: ErrorType?) {
        dataLoading.set(false)
        showMessage(errorMessage)
    }

    private fun makeProfessionList(profession: String?) {
        val list = ArrayList<String>()

        visaSearchQuery.visaSelection?.visaProducts?.get(selectedVisaType)?.requiredDocList?.forEach {
            it.name?.let { docName ->
                list.add(docName)
            }
        }

        val pair = Pair(profession, list)
        professionList.postValue(pair)
    }

    fun updateSelectedProfession(value: Int) {
        selectedProfessionPosition = value
        visaSearchQuery.visaSelection!!.visaProducts!![selectedVisaType].professionSelection =
            selectedProfessionPosition
    }

    private fun getProfession() = professionList.value!!.second[selectedProfessionPosition]

    private fun fetchProfileInfo() {
        dataLoading.set(true)
        val token = sharedPrefsHelper[PrefKey.ACCESS_TOKEN, ""]

        executeSuspendedCodeBlock { apiService.getProfileResponse(token) }
    }

    fun setQuickPickerData(position: Int) {
        val info = passengerList.value?.get(position)

        givenName.set(info?.givenName)
        surName.set(info?.surName)
        birthDate.set(info?.dateOfBirth)
        nationality.set(info?.nationality)
        email.set(info?.email)
        passportNumber.set(info?.passportNumber)
        passportExpiryDate.set(info?.passportExpireDate)

        info?.mobileNumber?.let {
            if (info.mobileNumber?.length == 14) {
                phone.set(info.mobileNumber!!.removeRange(0, 3))
            } else {
                phone.set(info.mobileNumber)
            }
        }

        if (info?.gender == "Female") {
            isMaleSelected.set(false)
        }
    }

    fun onClickNationality() {
        navigateToNationality.call()
    }

    fun onClickFemale() {
        isMaleSelected.set(false)
    }

    fun onClickMale() {
        isMaleSelected.set(true)
    }

    fun onTextChangedForFirstName(s: CharSequence, start: Int, before: Int, count: Int) {
        liveValidation.value = Pair(1, s.toString().isNameValid())
    }

    fun onTextChangedForLastName(s: CharSequence, start: Int, before: Int, count: Int) {
        liveValidation.value = Pair(2, s.toString().isNameValid())
    }

    fun onTextChangedForBirthday(s: CharSequence, start: Int, before: Int, count: Int) {
        liveValidation.value = Pair(3, s.toString().isNotEmpty())
    }

    fun onTextChangedForNationality(s: CharSequence, start: Int, before: Int, count: Int) {
        liveValidation.value = Pair(4, s.toString().isNotEmpty())
    }

    fun onTextChangedForCurrentAddress(s: CharSequence, start: Int, before: Int, count: Int) {
        liveValidation.value = Pair(5, s.toString().isNotEmpty())
    }

    fun onTextChangedForDestinationAddress(s: CharSequence, start: Int, before: Int, count: Int) {
        liveValidation.value = Pair(6, s.toString().isNotEmpty())
    }

    fun onTextChangedForPhoneNumber(s: CharSequence, start: Int, before: Int, count: Int) {
        liveValidation.value = Pair(7, phoneNumberCheck(s.toString()))
    }

    fun onTextChangedForEmail(s: CharSequence, start: Int, before: Int, count: Int) {
        liveValidation.value = Pair(8, s.toString().isEmailValid())
    }

    fun onTextChangedForPassportNumber(s: CharSequence, start: Int, before: Int, count: Int) {
        liveValidation.value = Pair(9, s.toString().isPassportNumberValid())
    }

    fun onTextChangedForPassportExpireDate(s: CharSequence, start: Int, before: Int, count: Int) {
        liveValidation.value = Pair(10, s.toString().isNotEmpty())
    }

    fun onTextChangedForContactAddress(s: CharSequence, start: Int, before: Int, count: Int) {
        liveValidation.value = Pair(12, s.toString().isNotEmpty())
    }

    fun onTextChangedForProfession(s: CharSequence, start: Int, before: Int, count: Int) {
        liveValidation.value = Pair(14, s.toString().isNotEmpty())
    }

    fun setCountryCode(code: String) {
        nationality.set(code)
    }

    fun onClickSaveMenu() {
        if (!isStickerVisa.get()) {
            if (!isInputValid())
                return

            itemTraveler.givenName = givenName.get()
            itemTraveler.surName = surName.get()
            itemTraveler.dateOfBirth = birthDate.get()
            itemTraveler.nationality = nationality.get()
            itemTraveler.passportNumber = passportNumber.get()
            itemTraveler.passportExpireDate = passportExpiryDate.get()
            itemTraveler.localAddress = currentAddress.get()
            itemTraveler.foreignAddress = foreignAddress.get()
            itemTraveler.mobileNumber = phone.get()
            itemTraveler.email = email.get()
            itemTraveler.profession = getProfession()
            itemTraveler.specialNotes = null

            if (isMaleSelected.get()) {
                itemTraveler.gender = "Male"
            } else {
                itemTraveler.gender = "Female"
            }

            visaSearchQuery.travellers[visaSearchQuery.currentTravellerPosition!!] = itemTraveler
            hideKeyboard.call()
            navigateToTravellerVerification.value = visaSearchQuery
        } else {
            if (!isInputValidForStickerVisa())
                return

            itemTraveler.givenName = givenName.get()
            itemTraveler.surName = surName.get()
            itemTraveler.mobileNumber = phone.get()
            itemTraveler.email = email.get()
            itemTraveler.localAddress = contactAddress.get()
            itemTraveler.specialNotes = specialNotes.get()
            itemTraveler.gender = null
            itemTraveler.dateOfBirth = null
            itemTraveler.profession = null
            itemTraveler.passportNumber = null
            itemTraveler.passportExpireDate = null
            itemTraveler.nationality = null
            itemTraveler.foreignAddress = null
            itemTraveler.requireDoc = null

            visaSearchQuery.travellers[visaSearchQuery.currentTravellerPosition!!] = itemTraveler
            hideKeyboard.call()
            navigateToTravellerVerification.value = visaSearchQuery
        }
    }

    private fun isInputValid(): Boolean {
        var value = true

        when {
            !givenName.get().isNameValid() -> {
                value = false
                dataInvalidPosition.postValue(0)
                showMessage(invalidGivenName)
            }
            TextUtils.isEmpty(givenName.get()) -> {
                value = false
                dataInvalidPosition.postValue(1)
                showMessage(enterGivenName)
            }
            TextUtils.isEmpty(surName.get()) -> {
                value = false
                dataInvalidPosition.postValue(2)
                showMessage(enterSurName)
            }
            TextUtils.isEmpty(birthDate.get()) -> {
                value = false
                dataInvalidPosition.postValue(3)
                showMessage(enterBirthday)
            }
            TextUtils.isEmpty(nationality.get()) -> {
                value = false
                dataInvalidPosition.postValue(4)
                showMessage(selectNationality)
            }
            TextUtils.isEmpty(currentAddress.get()) -> {
                value = false
                dataInvalidPosition.postValue(5)
                showMessage(enterAddress)
            }
            TextUtils.isEmpty(foreignAddress.get()) -> {
                value = false
                dataInvalidPosition.postValue(6)
                showMessage(enterForeignAddress)
            }
            !phoneNumberCheck(phone.get()) -> {
                value = false
                dataInvalidPosition.postValue(7)
                showMessage(invalidPhoneNumber)
            }
            TextUtils.isEmpty(email.get()) -> {
                value = false
                dataInvalidPosition.postValue(8)
                showMessage(enterEmailAddress)
            }
            TextUtils.isEmpty(passportNumber.get()) -> {
                value = false
                dataInvalidPosition.postValue(9)
                showMessage(enterPassportNumber)
            }
            !passportNumber.get().isPassportNumberValid() -> {
                value = false
                dataInvalidPosition.postValue(9)
                showMessage(invalidPassport)
            }
            TextUtils.isEmpty(passportExpiryDate.get()) -> {
                value = false
                dataInvalidPosition.postValue(10)
                showMessage(enterExpiryDate)
            }
            !Strings.isProperEmail(email.get()) -> {
                value = false
                dataInvalidPosition.postValue(11)
                showMessage(invalidEmail)
            }
            (selectedProfessionPosition == -1) -> {
                value = false
                dataInvalidPosition.postValue(14)
                showMessage(selectProfession)
            }
        }

        return value
    }

    private fun isInputValidForStickerVisa(): Boolean {
        var value = true

        when {
            !givenName.get().isNameValid() -> {
                value = false
                dataInvalidPosition.postValue(0)
                showMessage(invalidGivenName)
            }
            TextUtils.isEmpty(givenName.get()) -> {
                value = false
                dataInvalidPosition.postValue(1)
                showMessage(enterGivenName)
            }
            TextUtils.isEmpty(surName.get()) -> {
                value = false
                dataInvalidPosition.postValue(2)
                showMessage(enterSurName)
            }
            !phoneNumberCheck(phone.get()) -> {
                value = false
                dataInvalidPosition.postValue(7)
                showMessage(invalidPhoneNumber)
            }
            TextUtils.isEmpty(email.get()) -> {
                value = false
                dataInvalidPosition.postValue(8)
                showMessage(enterEmailAddress)
            }
            !Strings.isProperEmail(email.get()) -> {
                value = false
                dataInvalidPosition.postValue(11)
                showMessage(invalidEmail)
            }
            TextUtils.isEmpty(contactAddress.get()) -> {
                value = false
                dataInvalidPosition.postValue(12)
                showMessage(enterAddress)
            }
        }

        return value
    }

    private fun phoneNumberCheck(number: String?): Boolean {
        return if (number.isNullOrBlank()) {
            false
        } else {
            return RegexValidation.validRegex(number, "^(?:\\+88|01)?\\d{11}\$")
        }
    }
}
