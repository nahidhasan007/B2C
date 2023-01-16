package net.sharetrip.flight.booking.view.travellerdetails

import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import net.sharetrip.flight.booking.model.FlightSearch
import net.sharetrip.flight.booking.view.travellerdetails.adapter.TravelerAddAdapter

class TravelerDetailsViewModelFactory (val flightSearch: FlightSearch, val travelerAddAdapter: TravelerAddAdapter) :
    ViewModelProvider.Factory {
    override fun <T : ViewModel> create(modelClass: Class<T>): T {
        if (modelClass.isAssignableFrom(TravelerDetailsViewModel::class.java))
            return TravelerDetailsViewModel(
                flightSearch, travelerAddAdapter
            ) as T

        throw IllegalArgumentException("Unknown ViewModel class")
    }
}
