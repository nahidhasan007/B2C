package net.sharetrip.flight.history.view.refundselectpassenger

import androidx.databinding.ObservableBoolean
import androidx.lifecycle.MutableLiveData
import com.sharetrip.base.event.Event
import com.sharetrip.base.network.model.BaseResponse
import com.sharetrip.base.network.model.ErrorType
import com.sharetrip.base.network.model.RestResponse
import com.sharetrip.base.viewmodel.BaseOperationalViewModel
import net.sharetrip.flight.history.model.ApiCallingKey
import net.sharetrip.flight.history.model.RefundEligibleTravellersResponse
import net.sharetrip.flight.history.model.RefundQuotationResponse
import net.sharetrip.flight.history.model.Traveller
import net.sharetrip.flight.network.FlightHistoryApiService

class SelectPassengerViewModel(
    private val apiService: FlightHistoryApiService,
    private val bookingCode: String,
    private val token: String
) : BaseOperationalViewModel() {
    val passengers = mutableListOf<Int>()
    val eligibleTravelers = MutableLiveData<Event<List<Traveller>>>()
    lateinit var eligibleTravellerList: List<Traveller>
    val isPassengerFound = ObservableBoolean(true)
    val goToPricingDetails = MutableLiveData<Event<RefundQuotationResponse>>()
    val isLoading = ObservableBoolean()
    val selectAllClicked = MutableLiveData<Event<Boolean>>()

    init {
        isLoading.set(true)
        executeSuspendedCodeBlock(ApiCallingKey.GetEligibleTraveller.name) {
            apiService.getRefundEligibleTravellers(
                token,
                bookingCode
            )
        }
    }

    override fun onSuccessResponse(operationTag: String, data: BaseResponse.Success<Any>) {
        when (operationTag) {
            ApiCallingKey.GetEligibleTraveller.name -> {
                eligibleTravelers.postValue(
                    Event(((data.body as RestResponse<*>).response as RefundEligibleTravellersResponse).travellers)
                )
                eligibleTravellerList =
                    ((data.body as RestResponse<*>).response as RefundEligibleTravellersResponse).travellers

                if (eligibleTravellerList.isEmpty())
                    isPassengerFound.set(false)
            }

            ApiCallingKey.RefundQuotation.name -> {
                goToPricingDetails.postValue(Event((data.body as RestResponse<*>).response as RefundQuotationResponse))
            }
        }

        isLoading.set(false)
    }

    override fun onAnyError(operationTag: String, errorMessage: String, type: ErrorType?) {
        if (operationTag == ApiCallingKey.GetEligibleTraveller.name) {
            showMessage(errorMessage)
            isPassengerFound.set(false)
        } else if (operationTag == ApiCallingKey.RefundQuotation.name) {
            showMessage(errorMessage)
        }

        isLoading.set(false)
    }

    fun onNextClicked() {
        if (passengers.isEmpty())
            showMessage("Please Select a Passenger.")
        else {
            if (!isLoading.get()) {
                isLoading.set(true)
                val tickets = mutableListOf<String>()
                for (pos in passengers)
                    tickets.add(eligibleTravellerList[pos].eTicket)

                executeSuspendedCodeBlock(ApiCallingKey.RefundQuotation.name) {
                    apiService.refundQuotation(
                        token,
                        bookingCode,
                        tickets.toString()
                    )
                }
            }
        }
    }

    fun onPassengerSelected(pos: Int, isSelected: Boolean) {
        if (isSelected && pos !in passengers)
            passengers.add(pos)
        else if (!isSelected)
            passengers.remove(pos)
    }

    fun onSelectAllClicked() {
        passengers.clear()

        for (pos in eligibleTravellerList.indices)
            passengers.add(pos)

        selectAllClicked.postValue(Event(true))
    }
}
