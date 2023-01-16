package net.sharetrip.bus.booking.view.passengerInfo

import android.os.Bundle
import android.text.TextUtils
import androidx.databinding.ObservableBoolean
import androidx.databinding.ObservableField
import androidx.lifecycle.MutableLiveData
import com.google.gson.Gson
import com.sharetrip.base.data.PrefKey
import com.sharetrip.base.data.SharedPrefsHelper
import com.sharetrip.base.network.model.BaseResponse
import com.sharetrip.base.network.model.ErrorType
import com.sharetrip.base.network.model.RestResponse
import net.sharetrip.shared.utils.analytics.AnalyticsProvider
import net.sharetrip.shared.utils.dateChangeToDDMMYYYY
import net.sharetrip.shared.utils.isEmailValid
import net.sharetrip.shared.utils.isNameValid
import net.sharetrip.shared.utils.isPhoneNumberValid
import com.sharetrip.base.viewmodel.BaseOperationalViewModel
import net.sharetrip.bus.booking.model.*
import net.sharetrip.bus.booking.view.verifyInfo.BusPassengerVerifyInformationFragment.Companion.ARG_BUS_PASSENGER_VERIFY_DEPARTURE
import net.sharetrip.bus.booking.view.verifyInfo.BusPassengerVerifyInformationFragment.Companion.ARG_BUS_PASSENGER_VERIFY_TRAVELLER
import net.sharetrip.bus.network.BusBookingApiService
import net.sharetrip.bus.utils.MsgUtils.CHECK_ALL_INFORMATION
import net.sharetrip.bus.utils.SingleLiveEvent
import java.text.SimpleDateFormat
import java.util.*
import kotlin.math.abs

class BusPassengerInfoViewModel(
    var departureInfo: Departure,
    private val apiService: BusBookingApiService,
    private val sharedPrefsHelper: SharedPrefsHelper
) : BaseOperationalViewModel() {
    private val journeyDate: String = departureInfo.date!!
    val passengerList = MutableLiveData<List<Traveler>>()
    val passenger = ObservableField<ItemTraveler>()
    val givenName = ObservableField<String>()
    val surName = ObservableField<String>()
    val mobileNumber = ObservableField<String>()
    val email = ObservableField<String>()
    val liveValidation = SingleLiveEvent<Pair<PassengerValidation, Boolean>>()
    val navigateToPassengerVerification = SingleLiveEvent<Bundle>()
    var isMaleSelected = ObservableBoolean()
    var isQuickPickEnable = ObservableBoolean()
    var dialogLoading = MutableLiveData<Boolean>()
    var checkBoxClicked = MutableLiveData<Boolean>()
    var isNextEnabled = ObservableBoolean()
    private val busEventManager =
        AnalyticsProvider.busEventManager(AnalyticsProvider.getFirebaseAnalytics())

    init {
        fetchProfileInfo()
        isNextEnabled.set(false)
        isMaleSelected.set(true)
        checkBoxClicked.value = false
    }

    override fun onSuccessResponse(operationTag: String, data: BaseResponse.Success<Any>) {
        when (operationTag) {
            PassengerInfoApiCallingKey.ProfileInfo.name -> {
                val response = (data.body as RestResponse<*>).response as UserProfile
                isQuickPickEnable.set(response.otherPassengers.isEmpty())
                passengerList.value = response.otherPassengers
                dialogLoading.value = false
            }

            PassengerInfoApiCallingKey.AddPassenger.name -> {
                val response = data.body as RestResponse<*>

                if (response.code == "SUCCESS") {
                    setQuickPickerData(passengerList.value!!.size - 1)
                }

                fetchProfileInfo()
            }
        }
    }

    override fun onAnyError(operationTag: String, errorMessage: String, type: ErrorType?) {
        dialogLoading.value = false
        showMessage(errorMessage)
    }

    fun onClickMale() {
        isMaleSelected.set(true)
        updatePassenger()
    }

    fun onClickFemale() {
        isMaleSelected.set(false)
        updatePassenger()
    }

    fun onTextChangedForFirstName(s: CharSequence, start: Int, before: Int, count: Int) {
        val pair = Pair(PassengerValidation.GivenNameValidation, s.toString().isNameValid())
        liveValidation.value = pair
        passenger.get()?.givenName = givenName.get()
        isNextEnabled.set(isInputValid())
        updatePassenger()
    }

    fun onTextChangedForLastName(s: CharSequence, start: Int, before: Int, count: Int) {
        val pair = Pair(PassengerValidation.SurNameValidation, s.toString().isNameValid())
        liveValidation.value = pair
        passenger.get()?.surName = surName.get()
        isNextEnabled.set(isInputValid())
        updatePassenger()
    }

    fun onTextChangedForMobileNumber(s: CharSequence, start: Int, before: Int, count: Int) {
        val pair = Pair(PassengerValidation.MobileValidation, s.toString().isPhoneNumberValid())
        liveValidation.value = pair
        passenger.get()?.mobileNumber = mobileNumber.get()
        isNextEnabled.set(isInputValid())
        updatePassenger()
    }

    fun onTextChangedForEmail(s: CharSequence, start: Int, before: Int, count: Int) {
        passenger.get()?.email = email.get()
        isNextEnabled.set(isInputValid())
        val pair = Pair(PassengerValidation.EmailValidation, s.toString().isEmailValid())
        liveValidation.value = pair
        updatePassenger()
    }

    fun onCheckBoxClicked() {
        checkBoxClicked.value = !checkBoxClicked.value!!
    }

    private fun isInputValid(): Boolean {
        return (!TextUtils.isEmpty(surName.get())
                && surName.get()?.isNameValid() == true
                && mobileNumber.get()?.isPhoneNumberValid() == true
                && email.get()?.isEmailValid() == true
                )
    }

    fun onAddPassenger() {
        if (isInputValid()) {
            dialogLoading.value = true
            var gender = ""
            var title = ""

            if (isMaleSelected.get()) {
                gender = "Male"
                title = "Master"
            } else {
                gender = "Female"
                title = "Miss"
            }

            val paas = Traveler(
                titleName = title,
                givenName = givenName.get().toString(),
                surName = surName.get().toString(),
                mobileNumber = mobileNumber.get().toString(),
                email = email.get().toString(),
                dateOfBirth = "2021-01-01",
                nationality = "BD",
                gender = gender,
                passportCopy = "",
                passportExpireDate = "2022-01-01",
                passportNumber = "164461384413",
                visaCopy = "",
                frequentFlyerNumber = "26454685",
                code = "",
                isQuickPick = true
            )
            val token = sharedPrefsHelper[PrefKey.ACCESS_TOKEN, ""]

            executeSuspendedCodeBlock(PassengerInfoApiCallingKey.AddPassenger.name) {
                apiService.addTraveler(
                    token,
                    paas
                )
            }
        } else
            showMessage(CHECK_ALL_INFORMATION)
    }

    private fun fetchProfileInfo() {
        dialogLoading.value = true
        val token = sharedPrefsHelper[PrefKey.ACCESS_TOKEN, ""]

        executeSuspendedCodeBlock(PassengerInfoApiCallingKey.ProfileInfo.name) {
            apiService.getProfileResponse(
                token
            )
        }
    }

    fun setQuickPickerData(position: Int) {
        busEventManager.selectPassengerFromQuickPick()
        val info = passengerList.value?.get(position)
        val guest = ItemTraveler()
        guest.titleName = info?.titleName
        guest.givenName = info?.givenName
        givenName.set(info?.givenName)
        guest.surName = info?.surName
        surName.set(info?.surName)
        guest.gender = info?.gender

        try {
            guest.dateOfBirth = info!!.dateOfBirth.dateChangeToDDMMYYYY()
            guest.passportExpireDate = info.passportExpireDate.dateChangeToDDMMYYYY()

            val sdf = SimpleDateFormat("yyyy-MM-dd", Locale.ENGLISH)
            if (info.passportExpireDate.isNotEmpty()) {
                val firstDate = sdf.parse(info.passportExpireDate)
                val secondDate = sdf.parse(journeyDate)
                abs(secondDate.time - firstDate.time)
            }
        } catch (e: Exception) {
            guest.dateOfBirth = ""
            guest.passportExpireDate = ""
        }

        guest.nationality = info?.nationality
        guest.passportNumber = info?.passportNumber
        guest.passportCopy = info?.passportCopy
        guest.visaCopy = info?.visaCopy
        guest.mobileNumber = info?.mobileNumber
        mobileNumber.set(info?.mobileNumber)
        guest.email = info?.email
        email.set(info?.email)

        if (info?.gender == "Female")
            isMaleSelected.set(false)
        else
            isMaleSelected.set(true)

        passenger.set(guest)
        isNextEnabled.set(isInputValid())
    }

    fun updatePassenger() {
        val guest = ItemTraveler()
        guest.givenName = givenName.get()
        guest.surName = surName.get()
        if (isMaleSelected.get())
            guest.gender = "Male"
        else
            guest.gender = "Female"
        guest.mobileNumber = mobileNumber.get()
        guest.email = email.get()
        passenger.set(guest)
    }

    fun onNextClicked() {
        if (isNextEnabled.get()) {
            val g = Gson()
            val bundle = Bundle()
            bundle.putString(ARG_BUS_PASSENGER_VERIFY_TRAVELLER, g.toJson(passenger.get()))
            bundle.putParcelable(ARG_BUS_PASSENGER_VERIFY_DEPARTURE, departureInfo)
            navigateToPassengerVerification.value = bundle
        } else if (!isAllFieldsFilledUp()) {
            showMessage(CHECK_ALL_INFORMATION)
        }
    }

    fun isAllFieldsFilledUp(): Boolean {
        return (givenName.get()?.isEmpty() == false
                && surName.get()?.isEmpty() == false
                && mobileNumber.get()?.isEmpty() == false
                && email.get()?.isEmpty() == false)
    }
}
