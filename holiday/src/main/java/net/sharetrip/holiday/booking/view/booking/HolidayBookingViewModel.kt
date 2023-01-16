package net.sharetrip.holiday.booking.view.booking

import androidx.databinding.ObservableBoolean
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.viewModelScope
import com.example.holiday.R
import com.sharetrip.base.data.PrefKey
import com.sharetrip.base.data.SharedPrefsHelper
import com.sharetrip.base.event.Event
import net.sharetrip.shared.model.GuestLoginListener
import net.sharetrip.shared.model.GuestPopUpData
import com.sharetrip.base.network.model.Status
import com.sharetrip.base.viewmodel.BaseViewModel
import kotlinx.coroutines.launch
import net.sharetrip.holiday.booking.model.HolidayParam

class HolidayBookingViewModel(
    val holidayParam: HolidayParam,
    private val sharedPrefsHelper: SharedPrefsHelper,
    private val holidayBookingRepository: HolidayBookingRepository
) : BaseViewModel(), GuestLoginListener {

    val navigationFlag = MutableLiveData<Boolean>()
    val msg = MutableLiveData<String>()
    val isLoginLiveData = MutableLiveData<Boolean>()
    val withAirFare = ObservableBoolean()
    val tripCoin: LiveData<String> = holidayBookingRepository.tripCoin
    val apiStatus: LiveData<Status> = holidayBookingRepository.apiStatus
    val navigateToLogin = MutableLiveData<Event<Boolean>>()
    val popupData: GuestPopUpData by lazy {
        GuestPopUpData(
            R.string.common_title,
            R.string.holiday_body,
            R.drawable.ic_holiday_blue_24dp, this
        )
    }

    init {
        withAirFare.set(holidayParam.withAirFare)
    }

    fun setHolidayParamValues(
        arrivalType: String, arrivalTransportName: String, arrivalCode: String,
        arrivalTime: String, departureType: String, departureTransportName: String,
        departureCode: String, departureTime: String, pickupTime: String
    ) {
        holidayParam.apply {
            this.arrivalType = arrivalType
            this.arrivalTransportName = arrivalTransportName
            this.arrivalTransportCode = arrivalCode
            this.arrivalPickupTime = arrivalTime

            this.departureType = departureType
            this.departureTransportName = departureTransportName
            this.departureTransportCode = departureCode
            this.departureTime = departureTime
            this.pickupTime = pickupTime
        }
    }

    fun fetchUserProfile() {
        val token = sharedPrefsHelper[PrefKey.ACCESS_TOKEN, ""]
        viewModelScope.launch {
            holidayBookingRepository.getUserProfileFromRemoteSource(token)
        }
    }

    override fun onClickLogin() {
        navigateToLogin.value = Event(true)
    }

}
