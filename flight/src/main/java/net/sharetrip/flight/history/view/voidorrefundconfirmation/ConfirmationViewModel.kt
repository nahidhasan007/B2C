package net.sharetrip.flight.history.view.voidorrefundconfirmation

import androidx.databinding.ObservableBoolean
import androidx.lifecycle.MutableLiveData
import com.sharetrip.base.event.Event
import com.sharetrip.base.network.model.BaseResponse
import com.sharetrip.base.network.model.ErrorType
import com.sharetrip.base.viewmodel.BaseOperationalViewModel
import net.sharetrip.flight.history.model.ApiCallingKey
import net.sharetrip.flight.history.model.RefundConfirmBody
import net.sharetrip.flight.history.model.VoidSearchIdBody
import net.sharetrip.flight.network.FlightHistoryApiService

class ConfirmationViewModel(
    private val apiService: FlightHistoryApiService,
    private val refundSearchId: String?,
    private val voidSearchId: String?,
    private val requestType: String,
    private val token: String
) : BaseOperationalViewModel() {
    val goSuccessScreen = MutableLiveData<Event<Boolean>>()
    val isLoading = ObservableBoolean()

    override fun onSuccessResponse(operationTag: String, data: BaseResponse.Success<Any>) {
        isLoading.set(false)

        if (operationTag == ApiCallingKey.ConfirmRequest.name) {
            goSuccessScreen.postValue(Event(true))
        }
    }

    override fun onAnyError(operationTag: String, errorMessage: String, type: ErrorType?) {
        isLoading.set(false)
        showMessage(errorMessage)
    }

    fun onConfirmClicked() {
        if (!isLoading.get()) {
            isLoading.set(true)
            if (requestType == "refund") {
                executeSuspendedCodeBlock(ApiCallingKey.ConfirmRequest.name) {
                    apiService.confirmRefundRequest(token, RefundConfirmBody(refundSearchId!!))
                }
            } else {
                executeSuspendedCodeBlock(ApiCallingKey.ConfirmRequest.name) {
                    apiService.confirmVoidRequest(token, VoidSearchIdBody(voidSearchId!!))
                }
            }
        }
    }
}
