package net.sharetrip.holiday.history.view.detail

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import com.sharetrip.base.event.Event
import com.sharetrip.base.network.model.Status
import net.sharetrip.holiday.history.model.BookingCode
import net.sharetrip.holiday.history.model.BookingHolidayDetails
import net.sharetrip.holiday.network.HolidayHistoryApiService

class HolidayBookingDetailRepository(private val apiService: HolidayHistoryApiService) {

    private val _isDataLoading = MutableLiveData<Boolean>()
    val isDataLoading: LiveData<Boolean>
    get() = _isDataLoading

    private val _apiStatus = MutableLiveData<Status>()
    val apiStatus : LiveData<Status>
    get() = _apiStatus

    private val _bookingDetails = MutableLiveData<BookingHolidayDetails>()
    val bookingDetails: LiveData<BookingHolidayDetails>
    get() = _bookingDetails

    private val _bookingStatus = MutableLiveData<String>()
    val bookingStatus: LiveData<String>
    get() = _bookingStatus

    private val _hotels = MutableLiveData<String>()
    val hotels: LiveData<String>
    get() = _hotels

    private val _toastMessage = MutableLiveData<Event<String>>()
    val toastMessage: LiveData<Event<String>>
    get() = _toastMessage

    suspend fun getBookingDetails(token: String, bookingCode: String){
        _isDataLoading.value = true
        try {
            val data = apiService.getHolidayBookingDetails(token, bookingCode)
            _bookingDetails.value = data.response!!
            _bookingStatus.value = _bookingDetails.value?.bookingStatus
            _hotels.value = bookingDetails.value?.hotels
            _apiStatus.value = Status.SUCCESS
        } catch (e: Exception) {
            _toastMessage.value = Event(e.message.toString())
            _apiStatus.value = Status.FAILED
        }
        _isDataLoading.value = false
    }

    suspend fun cancelHolidayBooking(key: String, bookingCode: BookingCode){
        _isDataLoading.value = true
        try {
            val data = apiService.cancelHolidayBooking(key, bookingCode)
            _toastMessage.value = Event(data.message)
            _apiStatus.value = Status.SUCCESS
        } catch (e: Exception) {
            _toastMessage.value = Event(e.message.toString())
            _apiStatus.value = Status.FAILED
        }
        _isDataLoading.value = false
    }
}
