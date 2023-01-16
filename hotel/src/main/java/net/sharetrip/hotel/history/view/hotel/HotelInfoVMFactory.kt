package net.sharetrip.hotel.history.view.hotel

import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import net.sharetrip.hotel.history.model.HotelHistoryItem

class HotelInfoVMFactory(
    val hotelInfo: HotelHistoryItem?,
) : ViewModelProvider.Factory {
    override fun <T : ViewModel> create(modelClass: Class<T>): T {
        if (modelClass.isAssignableFrom(HotelInfoViewModel::class.java))
            return HotelInfoViewModel(hotelInfo) as T

        throw IllegalArgumentException("Unknown ViewModel class")
    }
}
