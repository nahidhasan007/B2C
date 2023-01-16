package net.sharetrip.bus.booking.view.verifyInfo

import android.os.Bundle
import androidx.databinding.ObservableBoolean
import androidx.lifecycle.MutableLiveData
import com.google.gson.Gson
import com.sharetrip.base.network.model.BaseResponse
import com.sharetrip.base.network.model.ErrorType
import com.sharetrip.base.network.model.RestResponse
import net.sharetrip.shared.utils.isEmailValid
import net.sharetrip.shared.utils.isPhoneNumberValid
import com.sharetrip.base.viewmodel.BaseOperationalViewModel
import net.sharetrip.bus.booking.model.*
import net.sharetrip.bus.booking.view.summary.BusBookingSummaryFragment.Companion.ARG_BUS_SUMMARY_DEPARTURE
import net.sharetrip.bus.booking.view.summary.BusBookingSummaryFragment.Companion.ARG_BUS_SUMMARY_PASSENGER_INFO
import net.sharetrip.bus.booking.view.summary.BusBookingSummaryFragment.Companion.ARG_BUS_SUMMARY_SEARCH_AND_TRIP_COIN
import net.sharetrip.bus.network.BusBookingApiService
import net.sharetrip.bus.utils.MsgUtils.FILL_IN_CORRECT_FORMAT
import net.sharetrip.bus.utils.MsgUtils.unKnownErrorMsg
import net.sharetrip.bus.utils.SingleLiveEvent

class BusPassengerVerifyInfoViewModel(
    var passengerInfo: ItemTraveler,
    var departureInfo: Departure,
    private val searchIdAndTripCoin: SearchIdAndTripCoin,
    private val apiService: BusBookingApiService
) : BaseOperationalViewModel() {
    val dialogLoading = SingleLiveEvent<Boolean>()
    val liveValidation = SingleLiveEvent<Pair<PassengerValidation, Boolean>>()
    val navigateToSummary = SingleLiveEvent<Bundle>()
    var phnNumber = MutableLiveData<String>()
    var mail = MutableLiveData<String>()
    var isEditClicked = MutableLiveData<Boolean>()
    var isEditTrue = ObservableBoolean(false)

    override fun onSuccessResponse(operationTag: String, data: BaseResponse.Success<Any>) {
        dialogLoading.value = false
        val response = data.body as RestResponse<*>
        if (response.code == "SUCCESS") {
            val cartResponse = response.response as CartLockResponse
            departureInfo.company = Company("", "", cartResponse.tripInfos.company, "")
            departureInfo.coachNo = cartResponse.tripInfos.coachNo
            val g = Gson()
            val bundle = Bundle()
            bundle.putString(ARG_BUS_SUMMARY_PASSENGER_INFO, g.toJson(passengerInfo))
            bundle.putParcelable(ARG_BUS_SUMMARY_DEPARTURE, departureInfo)
            bundle.putParcelable(ARG_BUS_SUMMARY_SEARCH_AND_TRIP_COIN, searchIdAndTripCoin)
            navigateToSummary.value = bundle
        } else {
            showMessage(unKnownErrorMsg)
        }
    }

    override fun onAnyError(operationTag: String, errorMessage: String, type: ErrorType?) {
        dialogLoading.value = false
        showMessage(errorMessage)
    }

    fun onPhnNumberChanged(s: CharSequence, start: Int, before: Int, count: Int) {
        val pair = Pair(PassengerValidation.MobileValidation, s.toString().isPhoneNumberValid())
        liveValidation.value = pair
        phnNumber.value = s.toString()
    }

    fun onEmailChanged(s: CharSequence, start: Int, before: Int, count: Int) {
        val pair = Pair(PassengerValidation.EmailValidation, s.toString().isEmailValid())
        liveValidation.value = pair
        mail.value = s.toString()
    }

    fun onEditClicked() {
        isEditClicked.value = true
        isEditTrue.set(true)
    }

    fun onNextClicked() {
        if (passengerInfo.mobileNumber.isPhoneNumberValid() && passengerInfo.email.isEmailValid())
            lockCart()
        else
            showMessage(FILL_IN_CORRECT_FORMAT)
    }

    fun lockCart() {
        val lockCart =
            RequestLockCart(
                searchId = searchIdAndTripCoin.searchId,
                coachId = departureInfo.id,
                passengers = Passengers(
                    passengerInfo.givenName!!,
                    passengerInfo.surName!!,
                    phoneNumber = passengerInfo.mobileNumber!!,
                    email = passengerInfo.email!!,
                    gender = passengerInfo.gender!!
                )
            )
        dialogLoading.value = true
        executeSuspendedCodeBlock { apiService.cartLock(lockCart) }
    }
}
