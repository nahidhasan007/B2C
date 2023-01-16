package net.sharetrip.visa.history.view.detail

import android.os.Bundle
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
import net.sharetrip.shared.utils.*
import net.sharetrip.visa.history.model.BookingID
import net.sharetrip.visa.history.model.VisaHistoryDetailsResponse

class VisaHistoryDetailsViewModel(
    private val bookingCode: String,
    sharedPrefsHelper: SharedPrefsHelper,
    private val repository: VisaHistoryDetailRepository
) : BaseViewModel() {

    val isDataLoading: LiveData<Boolean> = repository.isDataLoading
    val isRetryButtonShow = ObservableBoolean(false)
    val isResendVoucherButtonShow = ObservableBoolean(false)
    val isCancelableButtonShow = ObservableBoolean(false)
    var visaHistoryDetailsResponse :LiveData<VisaHistoryDetailsResponse> = repository.visaHistoryDetailResponse
    val isStickerVisaType = repository.isStickerVisaType
    val toastMessage : MutableLiveData<String> = repository.toastMessage
    val showAlert = MutableLiveData<Boolean>()
    val status = ObservableField<String>()
    val showStatusColor = MutableLiveData<Boolean>()
    var token: String = ""

    private val _navigateToCancellationPolicy = MutableLiveData<Event<Boolean>>()
    val navigateToCancellationPolicy: LiveData<Event<Boolean>>
    get() = _navigateToCancellationPolicy

    private val _navigateToPriceDetails = MutableLiveData<Event<Boolean>>()
    val navigateToPriceDetails: LiveData<Event<Boolean>>
    get() = _navigateToPriceDetails

    private val _navigateToContactInfo = MutableLiveData<Event<Boolean>>()
    val navigateToContactInfo: LiveData<Event<Boolean>>
    get() = _navigateToContactInfo

    private val _navigateToTravellerDetails = MutableLiveData<Event<Boolean>>()
    val navigateToTravellerDetails: LiveData<Event<Boolean>>
    get() = _navigateToTravellerDetails

    val navigateToPayment = MutableLiveData<Event<Bundle>>()

    private val apiStatus: LiveData<Status> = repository.apiStatus

    init {
        token = sharedPrefsHelper[PrefKey.ACCESS_TOKEN, ""]
        fetchVisaDetails()
    }

    private fun fetchVisaDetails() {
        viewModelScope.launch {
            repository.getVisaDetails(token, bookingCode)
            if (apiStatus.value == Status.SUCCESS){
                updateStatusAndVisibility(visaHistoryDetailsResponse.value!!)
            }
        }
    }

    /**
    > PaymentStatus = Paid, bookingStatus = Confirm and VisaStatus = Pending >> Cancel Booking is possible
    > When bookingStatus Confirm and paymentStatus Paid only this scenario user can request to resendVoucher
    > When bookingStatus Processing and paymentStatus UnPaid only this scenario user can request to retryPayment */

    private fun updateStatusAndVisibility(visa: VisaHistoryDetailsResponse) {
        if (visa.paymentStatus == VISA_PAYMENT_STATUS.PAID.value && visa.bookingStatus == VISA_BOOKING_STATUS.CONFIRM.value
            && visa.visaStatus == VISA_STATUS.PENDING.value
        ) {
            isResendVoucherButtonShow.set(true)
            isCancelableButtonShow.set(true)
        } else if (visa.paymentStatus == VISA_PAYMENT_STATUS.PAID.value && visa.bookingStatus == VISA_BOOKING_STATUS.CONFIRM.value) {
            isResendVoucherButtonShow.set(true)
        } else if (visa.paymentStatus == VISA_PAYMENT_STATUS.UNPAID.value && visa.bookingStatus == VISA_BOOKING_STATUS.PROCESSING.value) {
            isRetryButtonShow.set(true)
        }

        if (visa.paymentStatus == VISA_PAYMENT_STATUS.UNPAID.value) {

            status.set(VISA_PAYMENT_STATUS.UNPAID.value)

        } else if (visa.paymentStatus == VISA_PAYMENT_STATUS.PAID.value
            && visa.bookingStatus == VISA_BOOKING_STATUS.PENDING.value
        ) {

            status.set(VISA_BOOKING_STATUS.PENDING.value)

        } else if (visa.bookingStatus == VISA_BOOKING_STATUS.CANCELLED.value) {

            status.set(VISA_BOOKING_STATUS.CANCELLED.value)

        } else if (visa.paymentStatus == VISA_PAYMENT_STATUS.PAID.value && visa.bookingStatus == VISA_BOOKING_STATUS.PROCESSING.value) {

            status.set(VISA_BOOKING_STATUS.PROCESSING.value)

        } else if (visa.paymentStatus == VISA_PAYMENT_STATUS.PAID.value && visa.bookingStatus == VISA_BOOKING_STATUS.CONFIRM.value) {

            status.set("Successful")

        }
        showStatusColor.value = true
    }

    fun onClickCancelBooking() {
        viewModelScope.launch {
            repository.cancelVisaBooking(token, BookingID(bookingCode))
        }
    }

    fun onClickRetryBooking() {
        viewModelScope.launch {
            val data = repository.retryPaymentVisaBooking(token, BookingID(bookingCode))
            if (data != null && data.code == "SUCCESS") {
                val bundle = Bundle()
                bundle.putString(ARG_PAYMENT_URL, data.response.url)
                bundle.putString(SERVICE_TYPE, SERVICE_TYPE_VISA)
                navigateToPayment.value = Event(bundle)
            }
        }
    }

    fun onClickResendVoucher() {
        viewModelScope.launch {
            repository.resendPaymentVisaBooking(token, BookingID(bookingCode))
        }
    }

    fun onClickContactUs() {
        visaHistoryDetailsResponse.value?.primaryContact?.let {
            _navigateToContactInfo.value = Event(true)
        }
    }

    fun onClickTravellerDetails() {
        _navigateToTravellerDetails.value = Event(true)
    }

    fun onClickPriceInfo() {
        _navigateToPriceDetails.value = Event(true)
    }

    fun onClickCancellationPolicy() {
        if (visaHistoryDetailsResponse.value?.visaProductSnapshot?.cancellationPolicy != null) {
            _navigateToCancellationPolicy.value = Event(true)
        } else {
            toastMessage.value = "No data found!"
        }
    }

    fun showAlert() {
        showAlert.value = true
    }

}