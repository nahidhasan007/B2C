package net.sharetrip.bus.booking.view.locationpoints

import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import com.sharetrip.base.data.SharedPrefsHelper
import net.sharetrip.bus.booking.model.Departure
import net.sharetrip.bus.booking.model.SearchIdAndTripCoin
import net.sharetrip.bus.network.BusBookingApiService

class SelectionLocationVMFactory(
    private val departureInfo: Departure,
    private val searchIdAndTripCoin: SearchIdAndTripCoin,
    private val apiService: BusBookingApiService,
    private val sharedPrefsHelper: SharedPrefsHelper
) : ViewModelProvider.Factory {
    override fun <T : ViewModel> create(modelClass: Class<T>): T {
        return SelectLocationPointsViewModel(
            departureInfo,
            searchIdAndTripCoin,
            apiService,
            sharedPrefsHelper
        ) as T
    }
}
