package net.sharetrip.flight.booking.view.passenger

import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import com.sharetrip.base.data.SharedPrefsHelper
import net.sharetrip.flight.booking.model.FlightSearch
import net.sharetrip.flight.booking.model.ItemTraveler
import net.sharetrip.flight.booking.model.flightresponse.flights.Flights

class PassengerViewModelFactory(
    private val sharedPrefsHelper: SharedPrefsHelper,
    private var flights: Flights,
    private val flightSearch: FlightSearch,
    private var itemTraveler: ItemTraveler,
    private val flightDate: String
) :
    ViewModelProvider.Factory {
    override fun <T : ViewModel> create(modelClass: Class<T>): T {
        if (modelClass.isAssignableFrom(PassengerViewModel::class.java))
            return PassengerViewModel(
                sharedPrefsHelper,
                flights,
                flightSearch,
                itemTraveler,
                flightDate
            ) as T
        throw IllegalArgumentException("Unknown ViewModel class")
    }
}
