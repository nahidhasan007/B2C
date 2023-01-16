package net.sharetrip.hotel.booking.view.nationality

import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import com.sharetrip.base.data.SharedPrefsHelper
import net.sharetrip.hotel.network.HotelBookingApiService

class NationalityVMFactory(
    private val apiService: HotelBookingApiService,
    private val sharedPrefsHelper: SharedPrefsHelper
) : ViewModelProvider.Factory {
    override fun <T : ViewModel> create(modelClass: Class<T>): T {
        return NationalityViewModel(
            apiService,
            sharedPrefsHelper
        ) as T
    }
}
