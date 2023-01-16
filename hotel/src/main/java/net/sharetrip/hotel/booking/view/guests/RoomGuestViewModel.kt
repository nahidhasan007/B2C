package net.sharetrip.hotel.booking.view.guests

import android.content.Intent
import androidx.lifecycle.MutableLiveData
import com.sharetrip.base.data.PrefKey
import com.sharetrip.base.data.SharedPrefsHelper
import com.sharetrip.base.network.model.BaseResponse
import com.sharetrip.base.network.model.ErrorType
import com.sharetrip.base.network.model.RestResponse
import com.sharetrip.base.viewmodel.BaseOperationalViewModel
import net.sharetrip.hotel.booking.model.GuestInfo
import net.sharetrip.hotel.booking.model.Traveler
import net.sharetrip.hotel.booking.model.UserProfile
import net.sharetrip.hotel.booking.view.guests.RoomGuestFragment.Companion.ARG_GUEST_ROOM_NO
import net.sharetrip.hotel.network.HotelBookingApiService
import net.sharetrip.hotel.utils.MsgUtils.CHECK_ALL_INFORMATION
import net.sharetrip.hotel.utils.MsgUtils.INVALID_TRAVELLER_NAME
import net.sharetrip.hotel.utils.MsgUtils.INVALID_TRAVELLER_TITLE
import net.sharetrip.hotel.utils.SingleLiveEvent
import net.sharetrip.shared.utils.isNameValid

class RoomGuestViewModel(
    val guestList: ArrayList<GuestInfo>,
    private val roomNumber: Int,
    private val apiService: HotelBookingApiService,
    private val sharedPrefsHelper: SharedPrefsHelper
) : BaseOperationalViewModel() {

    private val validTitle = listOf("Mr", "Ms", "Mstr", "Mrs")
    val otherPassengerList = ArrayList<Traveler>()
    val passengerList = MutableLiveData<ArrayList<Traveler>>()
    var guestPosition: Int = 0
    val navigateBack = SingleLiveEvent<Intent>()
    val navigateToNationality = SingleLiveEvent<Any>()

    init {
        fetchProfileInfo()
    }

    override fun onSuccessResponse(operationTag: String, data: BaseResponse.Success<Any>) {
        dataLoading.set(false)
        val profileResponse = (data.body as RestResponse<*>).response as UserProfile
        passengerList.value = profileResponse.otherPassengers as ArrayList<Traveler>
    }

    override fun onAnyError(operationTag: String, errorMessage: String, type: ErrorType?) {
        dataLoading.set(false)
        showMessage(errorMessage)
    }

    private fun fetchProfileInfo() {
        val token = sharedPrefsHelper[PrefKey.ACCESS_TOKEN, ""]
        dataLoading.set(true)
        executeSuspendedCodeBlock { apiService.getProfileResponse(token) }
    }

    fun onClickSaveMenu() {
        for (it in guestList) {
            if (!it.isInputDataValid) {
                showMessage(CHECK_ALL_INFORMATION)
                return
            }

            if (it.titleName !in validTitle) {
                showMessage(INVALID_TRAVELLER_TITLE)
                return
            }

            if (!it.givenName.isNameValid() || !it.surName.isNameValid()) {
                showMessage(INVALID_TRAVELLER_NAME)
                return
            }
        }

        val intent = Intent()
        intent.putParcelableArrayListExtra(EXTRA_GUEST_LIST_MODEL, guestList)
        intent.putExtra(ARG_GUEST_ROOM_NO, roomNumber)
        navigateBack.value = intent
    }

    fun onClickNationality(guestPosition: Int) {
        this.guestPosition = guestPosition
        navigateToNationality.call()
    }

    companion object {
        const val EXTRA_GUEST_LIST_MODEL = "EXTRA_GUEST_LIST_MODEL"
    }
}
