package net.sharetrip.hotel.history.view.pricing

import com.sharetrip.base.viewmodel.BaseViewModel
import net.sharetrip.hotel.history.model.HotelHistoryItem

class PricingInfoViewModel(
    val historyItem: HotelHistoryItem
) : BaseViewModel() {
    val roomCount = historyItem.bookedRooms.size
    val nights = historyItem.totalNights
    private val bookedRooms = historyItem.bookedRooms
    val firstRoomInfo =
        "Room 1 " + bookedRooms[0].adults + "Adt, " + bookedRooms[0].childs + "Chd"
    val firstRoomCost = (bookedRooms[0].price).toString()

    val secondRoomInfo = if (bookedRooms.size > 1) "Room 2 " + bookedRooms[1].adults + "Adt, " +
            bookedRooms[1].childs + "Chd" else " "
    val secondRoomCost = if (bookedRooms.size > 1) (bookedRooms[1].price).toString() else " "

    val thirdRoomInfo = if (bookedRooms.size > 2) "Room 3 " + bookedRooms[2].adults + "Adt, " +
            bookedRooms[2].childs + "Chd" else " "
    val thirdRoomCost = if (bookedRooms.size > 2) (bookedRooms[2].price).toString() else " "

    val fourthRoomInfo = if (bookedRooms.size == 4) "Room 4 " + bookedRooms[3].adults + "Adt, " +
            bookedRooms[3].childs + "Chd" else " "
    val fourthRoomCost = if (bookedRooms.size == 4) (bookedRooms[3].price).toString() else " "

}
