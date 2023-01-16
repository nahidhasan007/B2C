package net.sharetrip.hotel.booking.view.hoteldetail

import android.os.Bundle
import android.view.View
import androidx.databinding.ObservableField
import androidx.lifecycle.MutableLiveData
import com.sharetrip.base.data.PrefKey
import com.sharetrip.base.data.SharedPrefsHelper
import com.sharetrip.base.network.model.BaseResponse
import com.sharetrip.base.network.model.ErrorType
import com.sharetrip.base.network.model.RestResponse
import com.sharetrip.base.viewmodel.BaseOperationalViewModel
import net.sharetrip.hotel.booking.model.HotelBookingApiCallingKey
import net.sharetrip.hotel.booking.model.HotelDetail
import net.sharetrip.hotel.booking.model.HotelDetailResponse
import net.sharetrip.hotel.booking.model.SearchSummary
import net.sharetrip.hotel.booking.view.roomlist.RoomListFragment.Companion.ARG_EARN_TRIP_COIN
import net.sharetrip.hotel.booking.view.roomlist.RoomListFragment.Companion.ARG_EXTRA_HOTEL_ID
import net.sharetrip.hotel.booking.view.roomlist.RoomListFragment.Companion.ARG_PROPERTY_NAME
import net.sharetrip.hotel.booking.view.roomlist.RoomListFragment.Companion.ARG_ROOM_EXTRA_HOTEL_SEARCH_CODE
import net.sharetrip.hotel.network.HotelBookingApiService
import net.sharetrip.hotel.utils.MoshiUtil
import net.sharetrip.hotel.utils.SingleLiveEvent
import net.sharetrip.shared.utils.analytics.AnalyticsProvider

class HotelDetailViewModel(
    private val hotelId: String,
    private val searchCode: String,
    val roomCount: Int,
    private val apiService: HotelBookingApiService,
    private val sharedPrefsHelper: SharedPrefsHelper
) : BaseOperationalViewModel() {
    private lateinit var hotelResponse: HotelDetailResponse
    private lateinit var searchSummary: SearchSummary
    private val hotelEventManager =
        AnalyticsProvider.hotelEventManager(AnalyticsProvider.getFirebaseAnalytics())
    val hotelDetails = MutableLiveData<HotelDetail>()
    val hotelDetail = ObservableField<HotelDetail>()
    val imageLink = MutableLiveData<ArrayList<String>>()
    val shareUrl = MutableLiveData<String>()
    var title = SingleLiveEvent<String>()
    var navigateToRoomList = SingleLiveEvent<Bundle>()
    var openMap = SingleLiveEvent<Any>()

    init {
        try {
            val summary = sharedPrefsHelper[PrefKey.HOTEL_SEARCH_SUMMARY, ""]
            searchSummary = MoshiUtil.convertStringToHotelSummary(summary)
            fetchHotelDetail()
        } catch (e: Exception) {
            e.printStackTrace()
        }
    }

    override fun onSuccessResponse(operationTag: String, data: BaseResponse.Success<Any>) {
        dataLoading.set(false)
        when (operationTag) {
            HotelBookingApiCallingKey.HOTEL_DETAILS.name -> {
                hotelResponse = (data.body as RestResponse<*>).response as HotelDetailResponse
                hotelDetails.value = hotelResponse.hotel
                hotelDetail.set(hotelResponse.hotel)

                searchSummary.hotelName = hotelResponse.hotel.name
                searchSummary.starRating = hotelResponse.hotel.starRating
                searchSummary.rating = hotelResponse.hotel.rating
                searchSummary.location = hotelResponse.hotel.contact[0].address.toString()
                searchSummary.kind = hotelResponse.hotel.kind

                sharedPrefsHelper.put(
                    PrefKey.HOTEL_SEARCH_SUMMARY,
                    MoshiUtil.convertHotelSummaryToString(searchSummary)
                )
                sharedPrefsHelper.put(
                    PrefKey.HOTEL_LOCATION,
                    hotelResponse.hotel.contact[0].region.name + ", " + hotelResponse.hotel.contact[0].region.countryCode
                )
                sharedPrefsHelper.put(
                    PrefKey.EARN_TRIP_COIN,
                    hotelResponse.hotel.points.earning.toString()
                )
                title.value = generateHotelName(hotelResponse.hotel.name)
                if (hotelResponse.hotel.images.isNotEmpty()) imageLink.postValue(hotelResponse.hotel.images as java.util.ArrayList<String>?)
            }

            HotelBookingApiCallingKey.SHARE_RESPONSE.name -> {
                val shareResponse = (data.body as RestResponse<*>).response as String
                shareUrl.value = shareResponse
            }
        }
    }

    override fun onAnyError(operationTag: String, errorMessage: String, type: ErrorType?) {
        dataLoading.set(false)
        showMessage(errorMessage)
    }

    fun openMap(view: View) {
        hotelEventManager
        openMap.call()
    }

    fun generateHotelName(name: String): String {
        return if (name.length > 19) name.substring(
            0,
            18
        ) + "..." else name
    }

    private fun fetchHotelDetail() {
        dataLoading.set(true)
        executeSuspendedCodeBlock(HotelBookingApiCallingKey.HOTEL_DETAILS.name) {
            apiService.getHotelDetailResponse(
                searchCode,
                hotelId
            )
        }
    }

    fun fetchShareUrl() {
        dataLoading.set(true)
        val token = sharedPrefsHelper[PrefKey.ACCESS_TOKEN, ""]
        executeSuspendedCodeBlock(HotelBookingApiCallingKey.SHARE_RESPONSE.name) {
            apiService.getShareResponse(token, "HOTEL", "OK")
        }
    }

    fun onClickSelectRoomButton() {
        hotelEventManager.selectRooms()
        val bundle = Bundle()
        bundle.putString(ARG_EXTRA_HOTEL_ID, hotelId)//hotelId
        bundle.putString(ARG_ROOM_EXTRA_HOTEL_SEARCH_CODE, searchCode)//searchId
        bundle.putString(ARG_EARN_TRIP_COIN, hotelDetails.value?.points?.earning.toString())
        bundle.putString(ARG_PROPERTY_NAME, hotelDetails.value?.name)
        navigateToRoomList.value = bundle
    }
}
