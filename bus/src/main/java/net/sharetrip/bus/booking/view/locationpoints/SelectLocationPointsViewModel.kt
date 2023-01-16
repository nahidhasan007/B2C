package net.sharetrip.bus.booking.view.locationpoints

import android.os.Bundle
import androidx.databinding.ObservableBoolean
import androidx.lifecycle.MutableLiveData
import com.sharetrip.base.data.PrefKey
import com.sharetrip.base.data.SharedPrefsHelper
import net.sharetrip.shared.model.GuestLoginListener
import net.sharetrip.shared.model.user.User
import com.sharetrip.base.network.model.BaseResponse
import com.sharetrip.base.network.model.ErrorType
import com.sharetrip.base.network.model.RestResponse
import com.sharetrip.base.viewmodel.BaseOperationalViewModel
import net.sharetrip.bus.booking.model.*
import net.sharetrip.bus.booking.view.passengerInfo.BusPassengerInfoFragment.Companion.ARG_BUS_PASSENGER_INFO_DEPARTURE_INFO
import net.sharetrip.bus.booking.view.passengerInfo.BusPassengerInfoFragment.Companion.ARG_BUS_PASSENGER_INFO_SEARCH_AND_TRIP_COIN
import net.sharetrip.bus.network.BusBookingApiService
import net.sharetrip.bus.utils.MsgUtils.loginFirst
import net.sharetrip.bus.utils.MsgUtils.selectBothDroppingPoint
import net.sharetrip.bus.utils.SingleLiveEvent
import java.text.NumberFormat
import java.util.*

class SelectLocationPointsViewModel(
    var departureInfo: Departure,
    private val searchIdAndTripCoin: SearchIdAndTripCoin,
    private val apiService: BusBookingApiService,
    private val sharedPrefsHelper: SharedPrefsHelper
) : BaseOperationalViewModel(), GuestLoginListener {
    val isLoggedIn = ObservableBoolean()
    val isLoginObserver = MutableLiveData<Boolean>()
    val observableUserTripCoin = SingleLiveEvent<String>()
    val navigateToPassengerInfo = SingleLiveEvent<Bundle>()
    var selected = 0
    var isNextClicked = MutableLiveData<Boolean>()
    var boardingPoint: Int? = null
    var droppingPoint: Int? = null
    var errorMessage = MutableLiveData<String>()
    var listPoints = mutableListOf<BoardingPoints>()
    var dialogLoading = SingleLiveEvent<Boolean>()
    var boardingPoints = ArrayList<BoardingPoints>()
    var droppingPoints = ArrayList<BoardingPoints>()

    init {
        departureInfo.boardingPoints?.forEach {
            val boardingPoint = BoardingPoints(
                it.counterName,
                it.reportingBranchId,
                it.reportingDate,
                it.reportingTime,
                it.scheduleTime
            )
            boardingPoints.add(boardingPoint)
        }

        departureInfo.droppingPoints?.forEach {
            val droppingPoint = BoardingPoints(
                it.counterName,
                it.reportingBranchId,
                it.reportingDate,
                it.reportingTime,
                it.scheduleTime
            )
            droppingPoints.add(droppingPoint)
        }

        checkLoggedIn()
    }

    override fun onSuccessResponse(operationTag: String, data: BaseResponse.Success<Any>) {
        dialogLoading.value = false
        when (operationTag) {
            SelectionLocationApiCallingKey.UserProfile.name -> {
                val response = (data.body as RestResponse<*>).response as User
                val tripCoin = NumberFormat.getNumberInstance(Locale.US)
                    .format(response.totalPoints.toLong())
                sharedPrefsHelper.put(PrefKey.USER_TRIP_COIN, tripCoin)
                observableUserTripCoin.value = tripCoin
                isLoginObserver.value = true
            }

            SelectionLocationApiCallingKey.UpdateCart.name -> {
                val response = (data.body as RestResponse<*>).response as SelectSeatRequestResponse
                departureInfo.cartId = response.id
                departureInfo.route = response.tripInfos?.route!!
                departureInfo.startCounter =
                    departureInfo.boardingPoints?.get(boardingPoint!!)?.counterName.toString()
                departureInfo.endCounter =
                    departureInfo.droppingPoints?.get(droppingPoint!!)?.counterName.toString()

                val bundle = Bundle()
                bundle.putParcelable(ARG_BUS_PASSENGER_INFO_DEPARTURE_INFO, departureInfo)
                bundle.putParcelable(
                    ARG_BUS_PASSENGER_INFO_SEARCH_AND_TRIP_COIN,
                    searchIdAndTripCoin
                )
                navigateToPassengerInfo.value = bundle
            }
        }
    }

    override fun onAnyError(operationTag: String, errorMessage: String, type: ErrorType?) {
        dialogLoading.value = false
         showMessage(errorMessage)
    }

    fun onClickNext() {
        if (isLoggedIn.get()) {
            if (boardingPoint != null && droppingPoint != null) {
                updateCart()
            } else if (selected == 0) {
                selected = 1
                isNextClicked.value = true
            } else
                errorMessage.value = selectBothDroppingPoint
        } else
            errorMessage.value = loginFirst
    }

    fun onPointSelected(pos: Int) {
        if (selected == 0)
            boardingPoint = pos
        else
            droppingPoint = pos
    }

    fun updateCart() {
        val selectRec =
            SelectSeatRequest(
                searchIdAndTripCoin.searchId,
                departureInfo.id,
                departureInfo.boardingPoints?.get(boardingPoint!!)?.reportingBranchId,
                departureInfo.droppingPoints?.get(droppingPoint!!)?.reportingBranchId,
                seats = departureInfo.selectedSeatId
            )
        dialogLoading.value = true

        executeSuspendedCodeBlock(SelectionLocationApiCallingKey.UpdateCart.name) {
            apiService.selectSeat(
                selectRec
            )
        }
    }

    fun checkLoggedIn() {
        isLoggedIn.set(sharedPrefsHelper[PrefKey.IS_LOGIN, false])
        isLoginObserver.value = isLoggedIn.get()
    }

    fun fetchUserProfile() {
        val token = sharedPrefsHelper[PrefKey.ACCESS_TOKEN, ""]
        dialogLoading.value = true

        executeSuspendedCodeBlock(SelectionLocationApiCallingKey.UserProfile.name) {
            apiService.getUserInformation(
                token
            )
        }
    }

    override fun onClickLogin() {
        /*val mRegistrationScreen = RegistrationScreen(false, isActivityResult = true)
        val fragmentTag = screenSwitcher.fragment
        if (fragmentTag != null) {
            activitySwitcher.openStartResult(mRegistrationScreen, 111, fragmentTag)
        }*/
        //TODO login
    }
}
