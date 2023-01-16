package net.sharetrip.hotel.booking.view.roomlist

import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import com.sharetrip.base.data.SharedPrefsHelper
import net.sharetrip.hotel.network.HotelBookingApiService

class RoomListVMFactory(
    private val searchCode: String,
    private val hotelId: String,
    private val tripCoin: String,
    private val propertyName: String?,
    private val sharedPrefsHelper: SharedPrefsHelper,
    private val apiService: HotelBookingApiService
) : ViewModelProvider.Factory {
    override fun <T : ViewModel> create(modelClass: Class<T>): T {
        return RoomListViewModel(
            apiService, searchCode,
            hotelId,
            tripCoin,
            sharedPrefsHelper, propertyName
        ) as T
    }
}
