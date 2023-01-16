package net.sharetrip.bus.booking.view.seatselection

import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import com.sharetrip.base.data.SharedPrefsHelper
import net.sharetrip.bus.booking.model.SearchIdAndTripCoin
import net.sharetrip.bus.network.BusBookingApiService

class BusSelectionVMFactory(
    private val departureTime: String?,
    private val arrivalTime: String?,
    private val discount: String,
    private val searchIdAndTripCoin: SearchIdAndTripCoin,
    private val apiService: BusBookingApiService,
    private val sharedPrefsHelper: SharedPrefsHelper
) : ViewModelProvider.Factory {
    override fun <T : ViewModel> create(modelClass: Class<T>): T {
        return BusSeatSelectionViewModel(
            departureTime,
            arrivalTime, discount, searchIdAndTripCoin,
            apiService,
            sharedPrefsHelper
        ) as T
    }
}
