package net.sharetrip.hotel.booking.view.travellerlist

import android.content.Intent
import android.os.Bundle
import com.sharetrip.base.data.PrefKey
import com.sharetrip.base.data.SharedPrefsHelper
import com.sharetrip.base.viewmodel.BaseViewModel
import net.sharetrip.hotel.booking.model.DiscountModel
import net.sharetrip.hotel.booking.model.GuestInfo
import net.sharetrip.hotel.booking.model.HotelBookingParams
import net.sharetrip.hotel.booking.view.guests.RoomGuestFragment.Companion.ARG_GUEST_ROOM_NO
import net.sharetrip.hotel.booking.view.guests.RoomGuestFragment.Companion.ARG_ROOM_GUEST_LIST
import net.sharetrip.hotel.booking.view.guests.RoomGuestViewModel.Companion.EXTRA_GUEST_LIST_MODEL
import net.sharetrip.hotel.booking.view.verification.HotelInfoVerificationFragment.Companion.ARG_VERIFICATION_DISCOUNT_MODEL
import net.sharetrip.hotel.booking.view.verification.HotelInfoVerificationFragment.Companion.ARG_VERIFICATION_HOTEL_BOOKING_PARAMS
import net.sharetrip.hotel.booking.view.verification.HotelInfoVerificationFragment.Companion.ARG_VERIFICATION_ROOM_BOOKING_SUMMARY
import net.sharetrip.hotel.utils.MoshiUtil
import net.sharetrip.hotel.utils.MsgUtils.CHECK_ALL_INFORMATION
import net.sharetrip.hotel.utils.SingleLiveEvent

class TravellerListViewModel(
    val hotelBookingParams: HotelBookingParams,
    private val roomSummary: String,
    private val discountModel: DiscountModel,
    sharedPrefsHelper: SharedPrefsHelper
) : BaseViewModel() {
    val observableUserTripCoin = SingleLiveEvent<String>()
    val navigateToGuestRoom = SingleLiveEvent<Bundle>()
    val navigateToHotelVerification = SingleLiveEvent<Bundle>()

    init {
        observableUserTripCoin.value = sharedPrefsHelper[PrefKey.USER_TRIP_COIN, ""]
    }

    fun handleTravellerData(data: Intent) {
        val roomNumber = data.getIntExtra(ARG_GUEST_ROOM_NO, 1)
        hotelBookingParams.rooms[0].guests[roomNumber - 1] =
            data.getParcelableArrayListExtra(EXTRA_GUEST_LIST_MODEL)!!
    }

    fun onClickFirstRoomTravellerInfo() {
        val bundle = Bundle()
        bundle.putParcelableArrayList(
            ARG_ROOM_GUEST_LIST,
            hotelBookingParams.rooms[0].guests[0] as ArrayList<GuestInfo>
        )
        bundle.putInt(ARG_GUEST_ROOM_NO, 1)
        navigateToGuestRoom.value = bundle
    }

    fun onClickSecondRoomTravellerInfo() {
        val bundle = Bundle()
        bundle.putParcelableArrayList(
            ARG_ROOM_GUEST_LIST,
            hotelBookingParams.rooms[0].guests[1] as ArrayList<GuestInfo>
        )
        bundle.putInt(ARG_GUEST_ROOM_NO, 2)
        navigateToGuestRoom.value = bundle
    }

    fun onClickThirdRoomTravellerInfo() {
        val bundle = Bundle()
        bundle.putParcelableArrayList(
            ARG_ROOM_GUEST_LIST,
            hotelBookingParams.rooms[0].guests[2] as ArrayList<GuestInfo>
        )
        bundle.putInt(ARG_GUEST_ROOM_NO, 3)
        navigateToGuestRoom.value = bundle
    }

    fun onClickFourthRoomTravellerInfo() {
        val bundle = Bundle()
        bundle.putParcelableArrayList(
            ARG_ROOM_GUEST_LIST,
            hotelBookingParams.rooms[0].guests[3] as ArrayList<GuestInfo>
        )
        bundle.putInt(ARG_GUEST_ROOM_NO, 4)
        navigateToGuestRoom.value = bundle
    }

    fun onClickNext() {
        var hasAllData = 0
        hotelBookingParams.rooms.forEach {
            it.guests.forEach { info ->
                info.forEach { guestInfo ->
                    if (!guestInfo.isInputDataValid) hasAllData -= 1
                }
            }
        }

        if (hasAllData < 0) {
            showMessage(CHECK_ALL_INFORMATION)
        } else {
            val bookingParam = MoshiUtil.convertBookingParamToString(hotelBookingParams)
            val bundle = Bundle()
            bundle.putString(ARG_VERIFICATION_HOTEL_BOOKING_PARAMS, bookingParam)
            bundle.putString(ARG_VERIFICATION_ROOM_BOOKING_SUMMARY, roomSummary)
            bundle.putParcelable(ARG_VERIFICATION_DISCOUNT_MODEL, discountModel)
            navigateToHotelVerification.value = bundle
        }
    }
}
