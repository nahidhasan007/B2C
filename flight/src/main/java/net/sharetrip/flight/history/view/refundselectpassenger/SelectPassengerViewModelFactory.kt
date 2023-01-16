package net.sharetrip.flight.history.view.refundselectpassenger

import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import net.sharetrip.flight.network.FlightHistoryApiService

class SelectPassengerViewModelFactory(
    private val apiService: FlightHistoryApiService,
    private val bookingCode: String,
    private val token:String
) : ViewModelProvider.Factory {
    override fun <T : ViewModel> create(modelClass: Class<T>): T {
        return SelectPassengerViewModel(apiService, bookingCode,token) as T
    }
}
