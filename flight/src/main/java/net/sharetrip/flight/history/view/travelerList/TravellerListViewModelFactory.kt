package net.sharetrip.flight.history.view.travelerList

import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import net.sharetrip.flight.history.model.FlightHistoryResponse

class TravellerListViewModelFactory(private val historyResponse: FlightHistoryResponse) :
    ViewModelProvider.Factory {

    override fun <T : ViewModel> create(modelClass: Class<T>): T {
        return TravellerListViewModel(historyResponse) as T
    }
}