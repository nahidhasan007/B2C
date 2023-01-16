package net.sharetrip.bus.booking.view.passengerInfo

import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import com.sharetrip.base.data.SharedPrefsHelper
import net.sharetrip.bus.booking.model.Departure
import net.sharetrip.bus.network.BusBookingApiService

class BusPassengerInfoVMFactory(
    private var departureInfo: Departure,
    private val apiService: BusBookingApiService,
    private val sharedPrefsHelper: SharedPrefsHelper
) : ViewModelProvider.Factory {
    override fun <T : ViewModel> create(modelClass: Class<T>): T {
        return BusPassengerInfoViewModel(
            departureInfo,
            apiService,
            sharedPrefsHelper
        ) as T
    }
}
