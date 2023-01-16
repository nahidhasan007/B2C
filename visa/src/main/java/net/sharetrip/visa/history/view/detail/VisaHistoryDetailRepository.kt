package net.sharetrip.visa.history.view.detail

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import com.sharetrip.base.network.model.RestResponse
import com.sharetrip.base.network.model.Status
import net.sharetrip.visa.booking.model.VisaType
import net.sharetrip.visa.history.model.BookingID
import net.sharetrip.visa.history.model.RetryPaymentVisaBookingResponse
import net.sharetrip.visa.history.model.VisaHistoryDetailsResponse
import net.sharetrip.visa.network.VisaHistoryApiService

class VisaHistoryDetailRepository(private val apiService: VisaHistoryApiService) {

    private val _isDataLoading = MutableLiveData<Boolean>()
    val isDataLoading: LiveData<Boolean>
    get() = _isDataLoading

    private val _apiStatus = MutableLiveData<Status>()
    val apiStatus: LiveData<Status>
    get() = _apiStatus

     val toastMessage = MutableLiveData<String>()

    private val _visaHistoryDetailResponse = MutableLiveData<VisaHistoryDetailsResponse>()
    val visaHistoryDetailResponse: LiveData<VisaHistoryDetailsResponse>
    get() = _visaHistoryDetailResponse

    private val _isStickerVisaType = MutableLiveData<Boolean>()
    val isStickerVisaType: LiveData<Boolean>
    get() = _isStickerVisaType

    suspend fun getVisaDetails(token: String, bookingCode: String){
        _isDataLoading.value = true
        try {
            val data = apiService.fetchVisaBookingHistoryDetails(token, bookingCode)
            _visaHistoryDetailResponse.value = data.response!!
            if (_visaHistoryDetailResponse.value!!.visaProductSnapshot?.type.equals(VisaType.StickerVisa.productName)){
                _isStickerVisaType.value = true
            }
            _apiStatus.value = Status.SUCCESS
        } catch (e: Exception) {
            e.printStackTrace()
            _apiStatus.value = Status.FAILED
        }

        _isDataLoading.value = false
    }

    suspend fun cancelVisaBooking(token: String, bookingID: BookingID){
        _isDataLoading.value = true

        try {
            val data = apiService.cancelVisaBooking(token, bookingID)
            toastMessage.value = data.message
        } catch (e: Exception) {
            e.printStackTrace()
        }

        _isDataLoading.value = false
    }

    suspend fun retryPaymentVisaBooking(token:String, bookingID: BookingID) : RestResponse<RetryPaymentVisaBookingResponse>? {
        _isDataLoading.value = true

        return try {
            val data =  apiService.retryPaymentVisaBooking(token, bookingID)
            _isDataLoading.value = false
            data
        } catch (e: Exception) {
            e.printStackTrace()
            _isDataLoading.value = false
            null
        }
    }

    suspend fun resendPaymentVisaBooking(token: String, bookingID: BookingID){
        _isDataLoading.value = true

        try {
            apiService.resendPaymentVisaBooking(token, bookingID)
            toastMessage.value = "Your voucher request has been successfully sent"
        } catch (e: Exception) {
            e.printStackTrace()
        }

        _isDataLoading.value = false
    }
}
