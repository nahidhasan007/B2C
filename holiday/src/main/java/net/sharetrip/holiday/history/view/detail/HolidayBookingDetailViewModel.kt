package net.sharetrip.holiday.history.view.detail

import androidx.databinding.ObservableBoolean
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.viewModelScope
import com.sharetrip.base.data.PrefKey
import com.sharetrip.base.data.SharedPrefsHelper
import com.sharetrip.base.event.Event
import com.sharetrip.base.network.model.Status
import net.sharetrip.shared.utils.HOTEL_BOOKING_STATUS
import net.sharetrip.shared.utils.PAYMENT_STATUS
import com.sharetrip.base.viewmodel.BaseViewModel
import kotlinx.coroutines.launch
import net.sharetrip.holiday.history.model.BookingCode
import net.sharetrip.holiday.history.model.BookingHolidayDetails

class HolidayBookingDetailViewModel(
    private val bookingCode: String,
    private val sharedPrefsHelper: SharedPrefsHelper,
    private val repository: HolidayBookingDetailRepository
): BaseViewModel() {

    private val _navigateToHolidayInfo = MutableLiveData<Event<Boolean>>()
    val navigateToHolidayInfo: LiveData<Event<Boolean>>
        get() = _navigateToHolidayInfo

    private val _navigateToPriceInfo = MutableLiveData<Event<Boolean>>()
    val navigateToPriceInfo: LiveData<Event<Boolean>>
        get() = _navigateToPriceInfo

    private val _navigateToHolidayContact = MutableLiveData<Event<Boolean>>()
    val navigateToHolidayContact: LiveData<Event<Boolean>>
        get() = _navigateToHolidayContact

    private val _navigateToCancelPolicy = MutableLiveData<Event<Boolean>>()
    val navigateToCancelPolicy: LiveData<Event<Boolean>>
        get() = _navigateToCancelPolicy

    private val _navigateToDashboard = MutableLiveData<Event<Boolean>>()
        val navigateToDashboard: LiveData<Event<Boolean>>
            get() = _navigateToDashboard

    var bookingDetails = repository.bookingDetails

    val isDataLoading = repository.isDataLoading
    val apiStatus = repository.apiStatus
    val isRetryButtonShow = ObservableBoolean(false)
    val isResendVoucherButtonShow = ObservableBoolean(false)
    val isCancelableButtonShow = ObservableBoolean(false)
    val bookingStatus: LiveData<String> = repository.bookingStatus
    val hotels: LiveData<String> = repository.hotels

    init {
        fetchBookingDetails()
    }

    private fun fetchBookingDetails() {
        val token = sharedPrefsHelper[PrefKey.ACCESS_TOKEN, ""]

        viewModelScope.launch {
            repository.getBookingDetails(token, bookingCode)
            if (apiStatus.value == Status.SUCCESS){
                updateStatusAndVisibility(bookingDetails.value!!)
            }
        }
    }

    private fun updateStatusAndVisibility(hotel: BookingHolidayDetails) {
        if (hotel.paymentStatus == PAYMENT_STATUS.PAID.value && hotel.bookingStatus == HOTEL_BOOKING_STATUS.COMPLETED.value) {
            isResendVoucherButtonShow.set(true)
            isCancelableButtonShow.set(true)
        } else if (hotel.paymentStatus == PAYMENT_STATUS.PAID.value && hotel.bookingStatus == HOTEL_BOOKING_STATUS.COMPLETED.value) {
            isResendVoucherButtonShow.set(true)
        } else if (hotel.paymentStatus == PAYMENT_STATUS.UNPAID.value && hotel.bookingStatus == HOTEL_BOOKING_STATUS.COMPLETED.value) {
            isRetryButtonShow.set(true)
            isCancelableButtonShow.set(true)
        }
    }

    fun onclickHolidayInformation() {
        _navigateToHolidayInfo.value = Event(true)
    }

    fun onClickPriceInfo() {
        _navigateToPriceInfo.value = Event(true)
    }

    fun onClickContactInfo() {
        _navigateToHolidayContact.value = Event(true)
    }

    fun onClickCancellationPolicy() {
        _navigateToCancelPolicy.value = Event(true)
    }

    fun onClickCancelBooking() {
        val token = sharedPrefsHelper[PrefKey.ACCESS_TOKEN, ""]
        val bookingCode = BookingCode(bookingDetails.value!!.bookingCode)

        viewModelScope.launch {
            repository.cancelHolidayBooking(token, bookingCode)
            if (apiStatus.value == Status.SUCCESS){
                _navigateToDashboard.value = Event(true)
            }
        }
    }

}
