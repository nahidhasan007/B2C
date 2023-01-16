package net.sharetrip.hotel.booking.view.roomlist

import android.annotation.SuppressLint
import android.os.Bundle
import android.os.Handler
import android.os.Looper
import androidx.databinding.ObservableBoolean
import androidx.lifecycle.MutableLiveData
import com.sharetrip.base.data.PrefKey
import com.sharetrip.base.data.SharedPrefsHelper
import com.sharetrip.base.network.model.BaseResponse
import com.sharetrip.base.network.model.ErrorType
import com.sharetrip.base.network.model.RestResponse
import com.sharetrip.base.viewmodel.BaseOperationalViewModel
import net.sharetrip.hotel.booking.model.*
import net.sharetrip.hotel.booking.view.hoteldiscount.HotelDiscountFragment.Companion.ARG_EXTRA_HOTEL_PARAM
import net.sharetrip.hotel.booking.view.hoteldiscount.HotelDiscountFragment.Companion.ARG_EXTRA_HOTEL_SUMMARY
import net.sharetrip.hotel.booking.view.roomdetail.RoomDetailFragment.Companion.ARG_HOTEL_ID
import net.sharetrip.hotel.booking.view.roomdetail.RoomDetailFragment.Companion.ARG_HOTEL_SEARCH_CODE
import net.sharetrip.hotel.booking.view.roomdetail.RoomDetailFragment.Companion.ARG_ROOM_DETAILS_IMAGES
import net.sharetrip.hotel.booking.view.roomdetail.RoomDetailFragment.Companion.ARG_ROOM_DETAIL_MODEL
import net.sharetrip.hotel.network.HotelBookingApiService
import net.sharetrip.hotel.utils.MoshiUtil
import net.sharetrip.hotel.utils.SingleLiveEvent
import net.sharetrip.shared.utils.analytics.AnalyticsProvider
import net.sharetrip.shared.utils.changeToString
import net.sharetrip.shared.utils.dateChangeToMMMDDYY
import java.text.SimpleDateFormat
import java.util.*

class RoomListViewModel(
    private val apiService: HotelBookingApiService,
    private val searchCode: String,
    private val hotelId: String,
    tripCoin: String,
    private val sharedPrefsHelper: SharedPrefsHelper,
    propertyName: String?
) : BaseOperationalViewModel(), RoomItemNavigator {
    private val hotelBookingParam: HotelBookingParams
    private val hotelEventManager =
        AnalyticsProvider.hotelEventManager(AnalyticsProvider.getFirebaseAnalytics())
    private val bookingSummary = RoomBookingSummary()
    private var roomNumberCount: Int = 0
    private var roomSearchCode: String = ""
    val roomListResponse = MutableLiveData<Pair<RoomListResponse, Int>>()
    val summary: MutableLiveData<RoomBookingSummary>
    val passenger: MutableLiveData<RoomBookingSummary>
    val isDataEmpty = ObservableBoolean(false)
    val showingFirstRoomData = ObservableBoolean(false)
    val navigateToRoomDetails = SingleLiveEvent<Bundle>()
    val navigateToHotelDiscount = SingleLiveEvent<Bundle>()
    val navigateBack = SingleLiveEvent<Any>()
    val observableUserTripCoin = SingleLiveEvent<String>()

    var promotionalCoupon: List<PromotionalCoupon>? = null

    init {
        fetchRoomResponse()
        hotelBookingParam = HotelBookingParams()
        hotelBookingParam.primaryContact = null
        try {
            bookingSummary.earnTripCoin = tripCoin.toInt()
        } catch (e: Exception) {
            bookingSummary.earnTripCoin = 0
        }
        bookingSummary.propertyName = propertyName
        summary = MutableLiveData()
        passenger = MutableLiveData()
        observableUserTripCoin.value = sharedPrefsHelper[PrefKey.USER_TRIP_COIN, ""]
    }

    override fun onSuccessResponse(operationTag: String, data: BaseResponse.Success<Any>) {
        val serverResponse = (data.body as RestResponse<*>).response as RoomListResponse
        if (serverResponse.rooms.isNotEmpty()) {
            val roomList =
                MoshiUtil.convertStringToTravellerRoomInfo(serverResponse.searchParams.rooms)
            hotelBookingParam.searchCode = serverResponse.roomSearchCode
            roomListResponse.value = Pair(serverResponse, roomList.size)
            promotionalCoupon = serverResponse.promotionalCoupon
            bookingSummary.totalNight = serverResponse.searchParams.totalNights
            bookingSummary.propertyCode =
                serverResponse.searchParams.propertyCode
            showingFirstRoomData.set(true)
            roomNumberCount = serverResponse.rooms.size
            roomSearchCode = serverResponse.roomSearchCode
            addRoomParam(roomList)
        } else {
            isDataEmpty.set(true)
        }
        dataLoading.set(false)
    }

    override fun onAnyError(operationTag: String, errorMessage: String, type: ErrorType?) {
        dataLoading.set(false)
        showMessage(errorMessage)
    }

    private fun fetchRoomResponse() {
        dataLoading.set(true)
        executeSuspendedCodeBlock {
            apiService.getRoomListResponse(
                true,
                searchCode,
                hotelId
            )
        }
    }

    private fun addRoomParam(roomList: List<TravellerRoomInfo>) {

        val roomParam = RoomParam()
        roomList.forEachIndexed { index, travellerRoomInfo ->
            val guestList = ArrayList<GuestInfo>()
            for (i in 1..travellerRoomInfo.numberOfAdult) guestList.add(GuestInfo().adultType)
            for (i in 1..travellerRoomInfo.childrenAges.size) guestList.add(GuestInfo().childType)
            roomParam.guests.add(guestList)

            when (index) {
                0 -> {
                    bookingSummary.firstRoomAdult = travellerRoomInfo.numberOfAdult
                    bookingSummary.firstRoomChild = travellerRoomInfo.childrenAges.size
                }

                1 -> {
                    bookingSummary.secondRoomAdult = travellerRoomInfo.numberOfAdult
                    bookingSummary.secondRoomChild = travellerRoomInfo.childrenAges.size
                }

                2 -> {
                    bookingSummary.thirdRoomAdult = travellerRoomInfo.numberOfAdult
                    bookingSummary.thirdRoomChild = travellerRoomInfo.childrenAges.size
                }

                3 -> {
                    bookingSummary.fourthRoomAdult = travellerRoomInfo.numberOfAdult
                    bookingSummary.fourthRoomChild = travellerRoomInfo.childrenAges.size
                }
            }
            Handler(Looper.getMainLooper()).postDelayed({
                passenger.value = bookingSummary
            }, 100)
        }
        hotelBookingParam.rooms.add(roomParam)
    }

    override fun openRoomDetails(images: List<String>, roomDetails: RoomDetails) {
        hotelEventManager.clickOnRoomDetails()
        val bundle = Bundle()
        bundle.putSerializable(ARG_ROOM_DETAILS_IMAGES, images as ArrayList<String>)
        bundle.putParcelable(ARG_ROOM_DETAIL_MODEL, roomDetails)
        bundle.putString(ARG_HOTEL_SEARCH_CODE, searchCode)
        bundle.putString(ARG_HOTEL_ID, hotelId)
        navigateToRoomDetails.value = bundle
    }

    @SuppressLint("SimpleDateFormat")
    override fun selectRoom(roomNo: Int, roomInfo: RoomBasicInfo) {
        val searchStr = sharedPrefsHelper[PrefKey.HOTEL_SEARCH_SUMMARY, ""]
        val searchParam = MoshiUtil.convertStringToHotelSummary(searchStr)
        val startDate = searchParam.dateRange.split("-")[0]
        val sdf = SimpleDateFormat("d MMM ''yy")
        val date: Date = sdf.parse(startDate) as Date
        val calendar = Calendar.getInstance()
        calendar.time = date

        val dateStringList = ArrayList<String>()
        dateStringList.add(calendar.time.changeToString().dateChangeToMMMDDYY())

        for (i in 1 until bookingSummary.totalNight) {
            calendar.add(Calendar.DATE, 1)
            dateStringList.add(calendar.time.changeToString().dateChangeToMMMDDYY())
        }

        bookingSummary.firstRoomCost = roomInfo.perNightPrice
        bookingSummary.firstRoomDiscountedCost = roomInfo.perNightDiscountedPrice
        bookingSummary.roomName = roomInfo.roomName
        bookingSummary.totalRoom = roomNo
        bookingSummary.dateList = dateStringList
        bookingSummary.gatewayType = roomInfo.gatewayType
        bookingSummary.providerCode = roomInfo.providerCode
        bookingSummary.rooms = roomInfo.rooms
        bookingSummary.searchId = searchCode
        bookingSummary.hotelId = hotelId
        bookingSummary.roomSearchCode = roomSearchCode
        bookingSummary.propertyRoomId = roomInfo.roomId

        hotelBookingParam.rooms[0].id = roomInfo.roomId

        val bookingParam = MoshiUtil.convertBookingParamToString(hotelBookingParam)
        val summary = MoshiUtil.convertBookingSummaryToString(bookingSummary)
        val bundle = Bundle()

        bundle.putString(ARG_EXTRA_HOTEL_PARAM, bookingParam)
        bundle.putString(ARG_EXTRA_HOTEL_SUMMARY, summary)

        navigateToHotelDiscount.value = bundle
    }

    fun onButtonRetry() {
        navigateBack.call()
    }
}
