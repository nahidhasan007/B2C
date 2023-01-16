package net.sharetrip.bus.booking.view.verifyInfo

import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import net.sharetrip.bus.booking.model.Departure
import net.sharetrip.bus.booking.model.ItemTraveler
import net.sharetrip.bus.booking.model.SearchIdAndTripCoin
import net.sharetrip.bus.network.BusBookingApiService

class BusPassengerVerifyVMFactory(
    private val passengerInfo: ItemTraveler,
    private val departureInfo: Departure,
    private val searchIdAndTripCoin: SearchIdAndTripCoin,
    private val apiService: BusBookingApiService
) : ViewModelProvider.Factory {
    override fun <T : ViewModel> create(modelClass: Class<T>): T {
        return BusPassengerVerifyInfoViewModel(
            passengerInfo,
            departureInfo,
            searchIdAndTripCoin,
            apiService
        ) as T
    }
}
