package net.sharetrip.hotel.history.view.bookingdetails

import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import com.sharetrip.base.data.SharedPrefsHelper
import net.sharetrip.hotel.history.model.HotelHistoryItem
import net.sharetrip.hotel.network.HotelHistoryApiService

class HotelHistoryDetailsVMFactory(
    private val sharedPrefsHelper: SharedPrefsHelper,
    val hotelInfo: HotelHistoryItem,
    private val repository: HotelHistoryApiService,
) : ViewModelProvider.Factory {
    override fun <T : ViewModel> create(modelClass: Class<T>): T {
        if (modelClass.isAssignableFrom(BookingDetailViewModel::class.java))
            return BookingDetailViewModel(sharedPrefsHelper, hotelInfo, repository) as T

        throw IllegalArgumentException("Unknown ViewModel class")
    }
}
