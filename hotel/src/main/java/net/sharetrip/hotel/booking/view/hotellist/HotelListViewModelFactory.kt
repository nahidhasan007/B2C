package net.sharetrip.hotel.booking.view.hotellist

import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import com.sharetrip.base.data.SharedPrefsHelper
import net.sharetrip.hotel.network.HotelBookingApiService
import net.sharetrip.hotel.utils.HotelSearchQuery

class HotelListViewModelFactory(
    private val searchData: HotelSearchQuery,
    private val apiService: HotelBookingApiService,
    private val sharedPrefsHelper: SharedPrefsHelper
) : ViewModelProvider.Factory {
    override fun <T : ViewModel> create(modelClass: Class<T>): T {
        return HotelListViewModel(searchData, apiService, sharedPrefsHelper) as T
    }
}
