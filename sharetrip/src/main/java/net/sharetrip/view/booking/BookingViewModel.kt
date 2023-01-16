package net.sharetrip.view.booking

import androidx.databinding.ObservableBoolean
import androidx.databinding.ObservableField
import androidx.lifecycle.MutableLiveData
import com.sharetrip.base.data.PrefKey
import com.sharetrip.base.data.SharedPrefsHelper
import com.sharetrip.base.event.Event
import net.sharetrip.shared.model.GuestLoginListener
import net.sharetrip.shared.model.GuestPopUpData
import com.sharetrip.base.viewmodel.BaseViewModel
import net.sharetrip.R
import java.text.NumberFormat
import java.util.*

class BookingViewModel(private val sharedPrefsHelper: SharedPrefsHelper) : BaseViewModel(),
    GuestLoginListener {

    val navigateToTourHistory = MutableLiveData<Event<Boolean>>()
    val gotoFlightBookingHistory = MutableLiveData<Event<Boolean>>()
    val gotoFlightHotelHistory = MutableLiveData<Event<Boolean>>()
    val gotoBusHistory = MutableLiveData<Event<Boolean>>()
    val gotoVisaHistory = MutableLiveData<Event<Boolean>>()
    val navigateToHoliday = MutableLiveData<Event<Boolean>>()
    val navigateToLogin = MutableLiveData<Event<Boolean>>()
    val isLogin = ObservableBoolean()
    var formattedPoints = ObservableField<String>()

    val popupData: GuestPopUpData by lazy {
        GuestPopUpData(
            R.string.travel_like_a_pro,
            R.string.please_log_in_or_sign_up_on_the_app_to_access_the_booking_history,
            R.drawable.ic_bookings_guest, this
        )
    }

    init {
        checkLoginInformation()
    }

    fun checkLoginInformation() {
        isLogin.set(sharedPrefsHelper[PrefKey.IS_LOGIN, false])

        var points = sharedPrefsHelper[PrefKey.USER_TRIP_COIN, ""]
        points = points.filter { it in '0'..'9' }
        if (points.isBlank()) {
            points = "0"
            sharedPrefsHelper.put(PrefKey.USER_TRIP_COIN, "0")
        }
        formattedPoints.set(NumberFormat.getNumberInstance(Locale.US).format(points.toInt()))
    }

    fun openHotel() {
        gotoFlightHotelHistory.value = Event(true)
    }

    fun openBus() {
        gotoBusHistory.value = Event(true)
    }

    fun openFlight() {
        gotoFlightBookingHistory.value = Event(true)
    }

    fun openHoliday() {
        navigateToHoliday.value = Event(true)
    }

    fun openTransfer() {
    }

    fun openTour() {
        navigateToTourHistory.value = Event(true)
    }

    fun openVisa() {
        gotoVisaHistory.value = Event(true)
    }

    override fun onClickLogin() {
        navigateToLogin.value = Event(true)
    }
}