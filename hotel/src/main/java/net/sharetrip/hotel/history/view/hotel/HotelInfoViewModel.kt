package net.sharetrip.hotel.history.view.hotel

import com.sharetrip.base.viewmodel.BaseViewModel
import net.sharetrip.hotel.history.model.Hotel
import net.sharetrip.hotel.history.model.HotelHistoryItem
import net.sharetrip.hotel.utils.SingleLiveEvent

class HotelInfoViewModel(val hotelInfo: HotelHistoryItem?) : BaseViewModel() {

    val hotel: Hotel? =  hotelInfo?.hotel
    val openMap = SingleLiveEvent<Any>()

    fun onClickedOpenMap() {
        openMap.call()
    }
}
