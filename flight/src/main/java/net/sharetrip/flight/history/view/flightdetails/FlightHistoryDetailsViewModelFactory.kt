package net.sharetrip.flight.history.view.flightdetails

import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import net.sharetrip.flight.history.model.FlightHistoryResponse

class FlightHistoryDetailsViewModelFactory (private val historyResponse : FlightHistoryResponse)
    : ViewModelProvider.Factory {

    override fun <T : ViewModel> create(modelClass: Class<T>): T {
        return FlightDetailsHistoryViewModel(historyResponse) as T
    }
}