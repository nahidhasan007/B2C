package net.sharetrip.hotel.history.view.roomdetails

import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import net.sharetrip.hotel.history.model.BookedRoom

class HotelHistoryRoomDetailsVMFactory(
    val roomInfo: BookedRoom?, val freeCancel: String?,
) : ViewModelProvider.Factory {
    override fun <T : ViewModel> create(modelClass: Class<T>): T {
        if (modelClass.isAssignableFrom(HotelHistoryRoomDetailViewModel::class.java))
            return HotelHistoryRoomDetailViewModel(roomInfo, freeCancel) as T

        throw IllegalArgumentException("Unknown ViewModel class")
    }
}
