package net.sharetrip.hotel.booking.view.travelleroom

import android.content.Intent
import androidx.lifecycle.MutableLiveData
import com.sharetrip.base.viewmodel.BaseViewModel
import net.sharetrip.hotel.booking.model.TravellerRoomInfo
import net.sharetrip.hotel.booking.view.travelleroom.TravellerRoomFragment.Companion.EXTRA_TRAVELER_ROOM_LIST

class TravellerRoomViewModel : BaseViewModel() {
    val listTravellerRooms = MutableLiveData<ArrayList<TravellerRoomInfo>>()

    fun onClickAddRoom() {
        val size = listTravellerRooms.value?.size

        if (size != null && size < 4) {
            val lists = listTravellerRooms.value
            lists?.add(TravellerRoomInfo())
            listTravellerRooms.value = lists!!
        }
    }

    fun onClickDone() {
        val intent = Intent()
        intent.putParcelableArrayListExtra(EXTRA_TRAVELER_ROOM_LIST, listTravellerRooms.value)
        navigateWithArgument("", intent)
    }

    fun bundleUpdate(rooms: ArrayList<TravellerRoomInfo>) {
        listTravellerRooms.value = rooms
    }
}
