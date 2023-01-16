package net.sharetrip.hotel.history.view.bookingdetails

import android.annotation.SuppressLint
import android.os.Bundle
import androidx.core.os.bundleOf
import androidx.databinding.ObservableBoolean
import androidx.databinding.ObservableField
import androidx.lifecycle.MutableLiveData
import com.sharetrip.base.data.PrefKey
import com.sharetrip.base.data.SharedPrefsHelper
import com.sharetrip.base.network.model.BaseResponse
import com.sharetrip.base.network.model.ErrorType
import com.sharetrip.base.network.model.RestResponse
import net.sharetrip.shared.utils.dateChangeToDDMMYYYYFromZ
import com.sharetrip.base.viewmodel.BaseOperationalViewModel
import net.sharetrip.hotel.booking.model.HotelBookingStatus
import net.sharetrip.hotel.booking.model.PaymentStatus
import net.sharetrip.hotel.history.model.BookingVoucher
import net.sharetrip.hotel.history.model.HotelHistoryItem
import net.sharetrip.hotel.history.model.RetryPaymentHotelResponse
import net.sharetrip.hotel.history.view.hotel.HotelInfoFragment
import net.sharetrip.hotel.history.view.pricing.PricingInfoFragment
import net.sharetrip.hotel.history.view.travelerlist.TravelerListFragment
import net.sharetrip.hotel.network.HotelHistoryApiService
import net.sharetrip.hotel.utils.HotelHistoryApiCallingKey
import net.sharetrip.hotel.utils.MsgUtils.OR_THE_BOOKING_WILL_BE_CANCELLED
import net.sharetrip.hotel.utils.MsgUtils.PLEASE_RETRY_BEFORE
import net.sharetrip.hotel.utils.MsgUtils.YOUR_BOOKING_HAS_BEEN_SUCCESSFULLY_CANCELLED
import net.sharetrip.hotel.utils.MsgUtils.YOUR_VOUCHER_REQUEST_HAS_BEEN_SUCCESSFULLY_SENT
import net.sharetrip.hotel.utils.MsgUtils.unKnownErrorMsg
import net.sharetrip.hotel.utils.SingleLiveEvent
import java.text.SimpleDateFormat
import java.util.*

class BookingDetailViewModel(
    val sharedPrefsHelper: SharedPrefsHelper,
    val hotelInfo: HotelHistoryItem,
    val apiService: HotelHistoryApiService,
) : BaseOperationalViewModel() {
    val isCancelableButtonShow = ObservableBoolean(false)
    val freeCancellationDate = ObservableField("")
    val totalPerson = ObservableField("")
    val showCancelAlert = MutableLiveData<Boolean>()
    val showStatusColor = MutableLiveData<Boolean>()
    val isRetryButtonShow = ObservableBoolean(false)
    val isResendVoucherButtonShow = ObservableBoolean(false)
    val navigateToPriceInfoFragment = SingleLiveEvent<Bundle>()
    val navigateToTravelerListFragment = SingleLiveEvent<Bundle>()
    val navigateToHotelInfoFragment = SingleLiveEvent<Bundle>()
    val navigateUp = SingleLiveEvent<Any>()
    val gotoPayment = SingleLiveEvent<String?>()

    init {
        updateStatusAndVisibility()
    }

    override fun onSuccessResponse(operationTag: String, data: BaseResponse.Success<Any>) {
        dataLoading.set(false)
        when (operationTag) {
            HotelHistoryApiCallingKey.CancelHotelBooking.name -> {
                showMessage(YOUR_BOOKING_HAS_BEEN_SUCCESSFULLY_CANCELLED)
                navigateUp.call()
            }

            HotelHistoryApiCallingKey.ResendVoucher.name -> {
                showMessage(YOUR_VOUCHER_REQUEST_HAS_BEEN_SUCCESSFULLY_SENT)
            }

            HotelHistoryApiCallingKey.RetryPayment.name -> {
                val retryPaymentData = data.body as RestResponse<*>
                if (retryPaymentData.message == "SUCCESS") {
                    gotoPayment.value = (retryPaymentData.response as RetryPaymentHotelResponse).url
                } else {
                    showMessage(unKnownErrorMsg)
                }
            }
        }
    }

    override fun onAnyError(operationTag: String, errorMessage: String, type: ErrorType?) {
        dataLoading.set(false)
        showMessage(errorMessage)
    }

    @SuppressLint("SimpleDateFormat")
    private fun updateStatusAndVisibility() {
        var adult = 0
        var child = 0

        hotelInfo.bookedRooms.forEach {
            adult += it.adults
            child += it.childs
        }

        totalPerson.set("$adult Adult $child Child")
        showStatusColor.value = true
        var isCancelable = false

        hotelInfo.freeCancellationDate?.let {
            val formatter = SimpleDateFormat("dd-MM-yyyy")
            freeCancellationDate.set(
                PLEASE_RETRY_BEFORE + hotelInfo.freeCancellationDate!!.dateChangeToDDMMYYYYFromZ()
                        + OR_THE_BOOKING_WILL_BE_CANCELLED
            )
            val date: Date? =
                formatter.parse(hotelInfo.freeCancellationDate!!.dateChangeToDDMMYYYYFromZ())
            val freeCancellationDate = Calendar.getInstance()
            val currentDate = Calendar.getInstance()
            date?.let {
                freeCancellationDate.time = it
                isCancelable = currentDate.before(freeCancellationDate)
            }
        }

        if (hotelInfo.paymentStatus == PaymentStatus.PAID.value && hotelInfo.status == HotelBookingStatus.COMPLETED.value && isCancelable) {
            isResendVoucherButtonShow.set(true)
            isCancelableButtonShow.set(true)
        } else if (hotelInfo.paymentStatus == PaymentStatus.PAID.value && hotelInfo.status == HotelBookingStatus.COMPLETED.value) {
            isResendVoucherButtonShow.set(true)
        } else if (hotelInfo.paymentStatus == PaymentStatus.UNPAID.value && hotelInfo.status == HotelBookingStatus.COMPLETED.value) {
            isRetryButtonShow.set(true)
            isCancelableButtonShow.set(true)
        }
    }

    fun showCancelAlert() {
        showCancelAlert.value = true
    }

    fun onClickCancelBooking() {
        dataLoading.set(true)
        val token = sharedPrefsHelper[PrefKey.ACCESS_TOKEN, ""]
        val voucher = BookingVoucher()
        voucher.voucherId = hotelInfo.voucherId
        executeSuspendedCodeBlock(HotelHistoryApiCallingKey.CancelHotelBooking.name) {
            apiService.cancelHotelBooking(token, voucher)
        }
    }

    fun onClickedResendVoucher() {
        dataLoading.set(true)
        val token = sharedPrefsHelper[PrefKey.ACCESS_TOKEN, ""]
        val bookingCode = hotelInfo.voucherId
        executeSuspendedCodeBlock(HotelHistoryApiCallingKey.ResendVoucher.name) {
            apiService.resendHotelVoucher(token, bookingCode)
        }
    }

    fun onClickedRetryPayment() {
        dataLoading.set(true)
        val token = sharedPrefsHelper[PrefKey.ACCESS_TOKEN, ""]
        executeSuspendedCodeBlock(HotelHistoryApiCallingKey.RetryPayment.name) {
            apiService.retryHotelPayment(token, hotelInfo.voucherId)
        }
    }

    fun onClickHotelInformation() {
        navigateToHotelInfoFragment.value =
            bundleOf(HotelInfoFragment.ARG_HOTEL_BOOKING_HOTEL_INFO to hotelInfo)
    }

    fun onClickTravellersInformation() {
        navigateToTravelerListFragment.value =
            bundleOf(TravelerListFragment.ARG_TRAVELER_LIST to hotelInfo.guests)
    }

    fun onClickPriceInfo() {
        navigateToPriceInfoFragment.value =
            bundleOf(PricingInfoFragment.ARG_HOTEL_PRICE_INFO to hotelInfo)
    }
}
