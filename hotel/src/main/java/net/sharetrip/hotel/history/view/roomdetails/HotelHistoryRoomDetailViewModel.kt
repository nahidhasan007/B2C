package net.sharetrip.hotel.history.view.roomdetails

import com.google.gson.Gson
import net.sharetrip.shared.utils.dateChangeToDDMMYYYYFromZ
import com.sharetrip.base.viewmodel.BaseViewModel
import net.sharetrip.hotel.history.model.BookedRoom
import net.sharetrip.hotel.history.model.Points

class HotelHistoryRoomDetailViewModel(val roomInfo: BookedRoom?, freeCancel: String?) :
    BaseViewModel() {
    var travellerCount = ""
    var freeCancelDate = ""
    var isRefundable = false
    var points = Points()

    init {
        travellerCount =
            roomInfo?.adults.toString() + " Adt, " + roomInfo?.childs.toString() + " Chd"
        freeCancel?.let {
            freeCancelDate = it.dateChangeToDDMMYYYYFromZ()
            isRefundable = true
        }
        points = Gson().fromJson(roomInfo?.points, Points::class.java)
    }

}
