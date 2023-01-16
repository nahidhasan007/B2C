package net.sharetrip.hotel.booking.view.roomlist

import net.sharetrip.hotel.booking.model.Rate

class RoomViewModel(val roomInfo: Rate, private val selector: RoomSelector) {
    fun onClickSelector() {
        selector.selectRoom(roomInfo)
    }
}
