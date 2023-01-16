package net.sharetrip.hotel.booking.view.travelleroom

import androidx.databinding.ObservableField
import net.sharetrip.hotel.booking.model.TravellerRoomInfo

class RoomViewModel(
    private val position: Int,
    private val roomInfo: TravellerRoomInfo,
    private val remover: RoomRemoveListener
) {
    val adultCount = ObservableField<Int>()
    val childCount = ObservableField<Int>()
    val roomNumberText = ObservableField<String>()
    val firstChildAge = ObservableField<String>()
    val secondChildAge = ObservableField<String>()

    init {
        val roomNo = position + 1
        roomNumberText.set("Room $roomNo (max 6 persons)")
        adultCount.set(roomInfo.numberOfAdult)
        childCount.set(roomInfo.numberOfChildren)
        firstChildAge.set("1")
        secondChildAge.set("5")
    }

    fun onClickAddAdult() {
        if (roomInfo.numberOfAdult < 4) {
            roomInfo.numberOfAdult += 1
            adultCount.set(roomInfo.numberOfAdult)
        }
    }

    fun onClickRemoveAdult() {
        if (roomInfo.numberOfAdult > 1) {
            roomInfo.numberOfAdult -= 1
            adultCount.set(roomInfo.numberOfAdult)
        }
    }

    fun onClickAddChild() {
        if (roomInfo.numberOfChildren < 2) {
            roomInfo.numberOfChildren += 1
            childCount.set(roomInfo.numberOfChildren)
        }
    }

    fun onClickRemoveChild() {
        if (roomInfo.numberOfChildren > 0) {
            roomInfo.numberOfChildren -= 1
            childCount.set(roomInfo.numberOfChildren)
        }
    }

    fun afterChangedFirstChildAge() {
        if (roomInfo.childrenAges.size == 0) {
            roomInfo.childrenAges.add(firstChildAge.get()?.toInt()!!)
        } else if (roomInfo.childrenAges.size > 0) {
            roomInfo.childrenAges[0] = firstChildAge.get()?.toInt()!!
        }
    }

    fun onChangedSecondChildAge() {
        if (roomInfo.childrenAges.size == 1) {
            roomInfo.childrenAges.add(secondChildAge.get()?.toInt()!!)
        } else if (roomInfo.childrenAges.size > 1) {
            roomInfo.childrenAges[1] = secondChildAge.get()?.toInt()!!
        }
    }

    fun onClickRemoveRoom() {
        remover.removeRoom(position)
    }
}
