package net.sharetrip.flight.history.view.bookingdetails

import android.os.Bundle
import androidx.lifecycle.MutableLiveData
import com.sharetrip.base.event.Event
import com.sharetrip.base.network.model.BaseResponse
import com.sharetrip.base.network.model.ErrorType
import com.sharetrip.base.network.model.RestResponse
import net.sharetrip.shared.utils.ARG_PAYMENT_URL
import net.sharetrip.shared.utils.DateUtil
import net.sharetrip.shared.utils.SERVICE_TYPE
import net.sharetrip.shared.utils.SERVICE_TYPE_FLIGHT
import com.sharetrip.base.viewmodel.BaseOperationalViewModel
import net.sharetrip.flight.history.model.ApiCallingKey
import net.sharetrip.flight.history.model.FlightHistoryResponse
import net.sharetrip.flight.history.model.RuleType
import net.sharetrip.flight.history.model.VoidQuotationResponse
import net.sharetrip.flight.history.model.localdatasource.UIMessageData.Companion.YOUR_BOOKING_HAS_BEEN_SUCCESSFULLY_CANCELLED
import net.sharetrip.flight.history.model.localdatasource.UIMessageData.Companion.YOUR_VOUCHER_REQUEST_HAS_BEEN_SUCCESSFULLY_SENT
import net.sharetrip.flight.network.DataManager
import net.sharetrip.flight.network.FlightHistoryApiService
import java.text.ParseException

class FlightBookingDetailsViewModel(
    val historyResponse: FlightHistoryResponse,
    private var token: String,
    private val apiService: FlightHistoryApiService
) : BaseOperationalViewModel() {
    var isLoaderVisible = MutableLiveData<Boolean>()
    var isShowCancelDialog = MutableLiveData<Boolean>()
    var bookingDate: Long? = null
    var returnBookingDate: Long? = null
    val gotoFlightDetails = MutableLiveData<Event<Boolean>>()
    val gotoTravellerInfo = MutableLiveData<Event<Boolean>>()
    val gotoPricingInfo = MutableLiveData<Event<Boolean>>()
    val gotoRule = MutableLiveData<Event<Int>>()
    val goBack = MutableLiveData<Event<Boolean>>()
    val gotoVoidConfirmation = MutableLiveData<Event<VoidQuotationResponse>>()

    val showToast: MutableLiveData<String> by lazy {
        MutableLiveData<String>()
    }

    override fun onSuccessResponse(operationTag: String, data: BaseResponse.Success<Any>) {
        when (operationTag) {
            ApiCallingKey.CancelFlight.name -> {
                isLoaderVisible.value = false
                showToast.value = YOUR_BOOKING_HAS_BEEN_SUCCESSFULLY_CANCELLED
                goBack.value = Event(true)
            }

            ApiCallingKey.ResendVoucher.name -> {
                isLoaderVisible.value = false
                showToast.value = YOUR_VOUCHER_REQUEST_HAS_BEEN_SUCCESSFULLY_SENT
            }

            ApiCallingKey.RetryPayment.name -> {
                isLoaderVisible.value = false
                val flightList = historyResponse.flight
                when (flightList.size) {
                    1 -> {
                        try {
                            bookingDate =
                                DateUtil.parseDateTimeToMillisecond(
                                    flightList[0].departureDateTime?.date,
                                    flightList[0].departureDateTime?.time
                                )
                        } catch (e: ParseException) {
                            e.printStackTrace()
                        }
                    }
                    2 -> {
                        try {
                            bookingDate =
                                DateUtil.parseDateTimeToMillisecond(
                                    flightList[0].departureDateTime?.date,
                                    flightList[0].departureDateTime?.time
                                )
                            returnBookingDate =
                                DateUtil.parseDateTimeToMillisecond(
                                    flightList[flightList.size - 1].departureDateTime?.date,
                                    flightList[flightList.size - 1].departureDateTime?.time
                                )
                        } catch (e: ParseException) {
                            e.printStackTrace()
                        }
                    }
                }
                val bundle = Bundle()
                bundle.putString(ARG_PAYMENT_URL, data.body.toString())
                bundle.putString(SERVICE_TYPE, SERVICE_TYPE_FLIGHT)
                navigateWithArgument(GOTO_PAYMENT, bundle)
            }

            ApiCallingKey.VoidQuotation.name -> {
                isLoaderVisible.value = false
                gotoVoidConfirmation.postValue(Event((data.body as RestResponse<*>).response as VoidQuotationResponse))
            }
        }
    }

    override fun onAnyError(operationTag: String, errorMessage: String, type: ErrorType?) {
        when (operationTag) {
            ApiCallingKey.CancelFlight.name -> {
                isLoaderVisible.value = false
                showToast.value = errorMessage
            }

            ApiCallingKey.ResendVoucher.name -> {
                isLoaderVisible.value = false
                showToast.value = errorMessage
            }

            ApiCallingKey.RetryPayment.name -> {
                isLoaderVisible.value = false
                showToast.value = errorMessage
            }

            ApiCallingKey.VoidQuotation.name -> {
                isLoaderVisible.value = false
                showMessage(errorMessage)
            }
        }
    }

    fun onClickedTravellersInformationButton() {
        gotoTravellerInfo.value = Event(true)
    }

    fun onClickedPricingInformationButton() {
        historyResponse.priceBreakdown?.covidAmount = historyResponse.covidAmount
        historyResponse.priceBreakdown?.luggageAmount = historyResponse.luggageAmount
        historyResponse.priceBreakdown?.advanceIncomeTax = historyResponse.advanceIncomeTax
        historyResponse.priceBreakdown?.convenienceFee = historyResponse.convenienceFee
        historyResponse.priceBreakdown?.VATOnConvenienceFee = historyResponse.VATOnConvenienceFee
        gotoPricingInfo.value = Event(true)
    }

    fun onClickedAirFareRulesButton() {
        gotoRule.value = Event(RuleType.AIR_FARE_RULE)
    }

    fun onClickedBaggageButton() {
        gotoRule.value = Event(RuleType.BAGGAGE)
    }

    fun onClickedFlightDetailsButton() {
        gotoFlightDetails.value = Event(true)
    }

    fun onClickedFareDetailsButton() {
        gotoRule.value = Event(RuleType.FARE_DETAILS)
    }

    fun onClickedCancelButton() {
        isShowCancelDialog.value = true
    }

    fun onDialogYesBtnClick() {
        isLoaderVisible.value = true
        executeSuspendedCodeBlock(ApiCallingKey.CancelFlight.name) {
            DataManager.flightHistoryApiService.cancelFlight(token, historyResponse.bookingCode)
        }
    }

    fun onClickedResendVoucher() {
        isLoaderVisible.value = true
        executeSuspendedCodeBlock(ApiCallingKey.ResendVoucher.name) {
            DataManager.flightHistoryApiService.resendVoucher(token, historyResponse.bookingCode)
        }
    }

    fun onClickedRetryPayment() {
        isLoaderVisible.value = true
        executeSuspendedCodeBlock(ApiCallingKey.RetryPayment.name) {
            DataManager.flightHistoryApiService.retryPayment(token, historyResponse.bookingCode)
        }
    }

    fun onClickVoid() {
        isLoaderVisible.value = true
        executeSuspendedCodeBlock(ApiCallingKey.VoidQuotation.name) {
            apiService.voidQuotation(token, historyResponse.bookingCode, historyResponse.searchId)
        }
    }

    companion object {
        const val GOTO_PAYMENT = "GOTO_PAYMENT"
    }
}
