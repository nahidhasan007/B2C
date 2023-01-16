package net.sharetrip.hotel.booking.view.guests

import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import com.sharetrip.base.data.SharedPrefsHelper
import net.sharetrip.hotel.booking.model.GuestInfo
import net.sharetrip.hotel.network.HotelBookingApiService

class RoomGuestVMFactory(
    private val guests: ArrayList<GuestInfo>,
    private val roomNumber: Int,
    private val apiService: HotelBookingApiService,
    private val sharedPrefsHelper: SharedPrefsHelper
) : ViewModelProvider.Factory {
    override fun <T : ViewModel> create(modelClass: Class<T>): T {
        return RoomGuestViewModel(
            guests,
            roomNumber,
            apiService,
            sharedPrefsHelper
        ) as T
    }
}
