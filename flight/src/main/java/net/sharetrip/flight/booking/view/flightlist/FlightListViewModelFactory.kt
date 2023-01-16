package net.sharetrip.flight.booking.view.flightlist

import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import net.sharetrip.flight.booking.model.FlightSearch

class FlightListViewModelFactory(
    private val flightSearch: FlightSearch,
    private val repository: FlightListRepository
) : ViewModelProvider.Factory {
    override fun <T : ViewModel> create(modelClass: Class<T>): T {
        if (modelClass.isAssignableFrom(FlightListViewModel::class.java))
            return FlightListViewModel(flightSearch, repository) as T

        throw IllegalArgumentException("Unknown ViewModel class")
    }
}
