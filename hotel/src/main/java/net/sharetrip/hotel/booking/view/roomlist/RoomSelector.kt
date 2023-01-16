package net.sharetrip.hotel.booking.view.roomlist

import net.sharetrip.hotel.booking.model.Rate

interface RoomSelector {
    fun selectRoom(roomInfo: Rate)
}
