package net.sharetrip.flight.booking.view.searchairport

import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import net.sharetrip.flight.booking.view.searchairport.data.AirportDao

class SearchAirportViewModelFactory(val repo: SearchAirportRepo) :
    ViewModelProvider.Factory {

    override fun <T : ViewModel> create(modelClass: Class<T>): T {
        if (modelClass.isAssignableFrom(SearchAirportViewModel::class.java))
            return SearchAirportViewModel(repo) as T

        throw IllegalArgumentException("Unknown ViewModel class")
    }
}
