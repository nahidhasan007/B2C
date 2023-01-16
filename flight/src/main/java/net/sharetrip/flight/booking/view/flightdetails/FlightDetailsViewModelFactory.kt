package net.sharetrip.flight.booking.view.flightdetails

import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import com.sharetrip.base.data.SharedPrefsHelper
import net.sharetrip.flight.booking.model.FlightSearch
import net.sharetrip.flight.booking.model.flightresponse.flights.Flights
import net.sharetrip.flight.network.FlightBookingApiService

class FlightDetailsViewModelFactory(
    private val flightSearch: FlightSearch,
    private val mFlights: Flights,
    private val sharedPrefsHelper: SharedPrefsHelper,
    private val apiService: FlightBookingApiService
) : ViewModelProvider.Factory {
    override fun <T : ViewModel> create(modelClass: Class<T>): T {
        if (modelClass.isAssignableFrom(FlightDetailsViewModel::class.java))
            return FlightDetailsViewModel(
                flightSearch, mFlights, sharedPrefsHelper, apiService
            ) as T

        throw IllegalArgumentException("Unknown ViewModel class")
    }
}
