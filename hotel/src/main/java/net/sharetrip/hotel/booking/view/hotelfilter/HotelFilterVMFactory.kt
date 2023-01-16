package net.sharetrip.hotel.booking.view.hotelfilter

import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider

class HotelFilterVMFactory : ViewModelProvider.Factory {
    override fun <T : ViewModel> create(modelClass: Class<T>): T {
        return HotelFilterViewModel() as T
    }
}
