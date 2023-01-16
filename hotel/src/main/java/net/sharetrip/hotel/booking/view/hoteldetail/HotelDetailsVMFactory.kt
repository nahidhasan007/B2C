package net.sharetrip.hotel.booking.view.hoteldetail

import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import com.sharetrip.base.data.SharedPrefsHelper
import net.sharetrip.hotel.network.HotelBookingApiService

class HotelDetailsVMFactory(
    private val hotelId: String,
    private val searchCode: String,
    private val roomCount: Int,
    private val apiService: HotelBookingApiService,
    private val sharedPrefsHelper: SharedPrefsHelper
) : ViewModelProvider.Factory {
    override fun <T : ViewModel> create(modelClass: Class<T>): T {
        return HotelDetailViewModel(
            hotelId,
            searchCode,
            roomCount,
            apiService,
            sharedPrefsHelper
        ) as T
    }
}
