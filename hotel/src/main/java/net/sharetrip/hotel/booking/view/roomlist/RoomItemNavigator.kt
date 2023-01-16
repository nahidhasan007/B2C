package net.sharetrip.hotel.booking.view.roomlist

import net.sharetrip.hotel.booking.model.RoomBasicInfo
import net.sharetrip.hotel.booking.model.RoomDetails

interface RoomItemNavigator {

    fun openRoomDetails(images: List<String>, roomDetails: RoomDetails)

    fun selectRoom(roomNo: Int, roomInfo: RoomBasicInfo)
}
