package net.sharetrip.flight.history.view.list

import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import net.sharetrip.flight.network.FlightHistoryApiService

class FlightHistoryListViewModelFactory(private val token: String, private val repo: FlightHistoryListRepo)
    : ViewModelProvider.Factory {
    override fun <T : ViewModel> create(modelClass: Class<T>): T {
        return FlightHistoryListViewModel(token, repo) as T
    }
}
