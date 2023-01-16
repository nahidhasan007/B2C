package net.sharetrip.flight.history.view.bookingdetails

import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import net.sharetrip.flight.history.model.FlightHistoryResponse
import net.sharetrip.flight.network.FlightHistoryApiService

class FlightBookingDetailsViewModelFactory(
    private val historyResponse: FlightHistoryResponse,
    private var token: String,
    private val apiService: FlightHistoryApiService
) : ViewModelProvider.Factory {
    override fun <T : ViewModel> create(modelClass: Class<T>): T {
        return FlightBookingDetailsViewModel(historyResponse, token,apiService) as T
    }
}