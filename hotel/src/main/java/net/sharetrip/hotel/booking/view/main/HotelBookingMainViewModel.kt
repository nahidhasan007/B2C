package net.sharetrip.hotel.booking.view.main

import android.content.Intent
import androidx.databinding.ObservableBoolean
import androidx.databinding.ObservableField
import androidx.databinding.ObservableInt
import androidx.lifecycle.MutableLiveData
import com.sharetrip.base.data.PrefKey
import com.sharetrip.base.data.SharedPrefsHelper
import com.sharetrip.base.event.Event
import com.sharetrip.base.network.model.BaseResponse
import com.sharetrip.base.network.model.ErrorType
import com.sharetrip.base.network.model.RestResponse
import com.sharetrip.base.viewmodel.BaseOperationalViewModel
import net.sharetrip.hotel.booking.model.*
import net.sharetrip.hotel.booking.model.localdatasource.UIMessageData.Companion.POS_1_of_5
import net.sharetrip.hotel.booking.model.localdatasource.UIMessageData.Companion.SELECT_DESTINATION
import net.sharetrip.hotel.booking.view.travelleroom.TravellerRoomFragment.Companion.EXTRA_TRAVELER_ROOM_LIST
import net.sharetrip.hotel.network.DataManager
import net.sharetrip.hotel.utils.HotelSearchQuery
import net.sharetrip.hotel.utils.MoshiUtil
import net.sharetrip.shared.model.CalenderData
import net.sharetrip.shared.model.ServiceType
import net.sharetrip.shared.utils.DateUtil
import net.sharetrip.shared.utils.EXTRA_END_DATE_IN_MILLISECOND
import net.sharetrip.shared.utils.EXTRA_START_DATE_IN_MILLISECOND
import net.sharetrip.shared.utils.analytics.AnalyticsProvider
import java.text.NumberFormat
import java.text.ParseException
import java.util.*

class HotelBookingMainViewModel(val sharedPrefsHelper: SharedPrefsHelper) :
    BaseOperationalViewModel(), WishListTripNavigator {
    private var property: HotelProperty? = null
    val queryModel = HotelSearchQuery()
    private val searchSummary = SearchSummary()
    private var perWinCoin = 0
    private val showWishListContainer = ObservableBoolean(false)
    private val hotelEventManager =
        AnalyticsProvider.hotelEventManager(AnalyticsProvider.getFirebaseAnalytics())
    private var calenderData = CalenderData(-1L, mDateHintText = "")

    val destination = ObservableField<String>()
    val dateRangeData = ObservableField<String>()
    val travellerAndRoomData = ObservableField<String>()
    val position = ObservableField<String>()
    val promotionalImage = ObservableField("")

    val isFlightPromotionAvailable = ObservableBoolean(false)
    val bonusPoint = ObservableInt()
    val propertySelection = MutableLiveData<Event<Boolean>>()
    val liveCities = MutableLiveData<List<ReviewCity>>()
    val scrollingPosition = MutableLiveData<Int>()
    val noInternet = MutableLiveData<Boolean>()

    init {
        queryModel.initQueryValue()
        destination.set(SELECT_DESTINATION)
        position.set(POS_1_of_5)

        val startDate = DateUtil.parseDisplayDateFormatFromApiDateFormat(queryModel.checkin)
        val endDate = DateUtil.parseDisplayDateFormatFromApiDateFormat(queryModel.checkout)
        dateRangeData.set("$startDate - $endDate")
        searchSummary.dateRange = dateRangeData.get()!!
        travellerAndRoomData.set(generateTravelersAndRoomsInfo())
        noInternet.value = false
        getFlightPromotion()
        fetchPopularTourCity()
    }

    override fun onSuccessResponse(operationTag: String, data: BaseResponse.Success<Any>) {
        when (operationTag) {
            ApiCallingKey.PopularTourCity.name -> {
                val response = (data.body as RestResponse<*>).response as WannaGoResponse
                if (response.cities.isNotEmpty()) {
                    liveCities.postValue(response.cities)
                    showWishListContainer.set(true)
                    position.set("1/" + response.cities.size)
                    perWinCoin = response.perWinEarn
                } else {
                    showWishListContainer.set(false)
                }
                bonusPoint.set(response.perWinEarn)
            }

            ApiCallingKey.SubmitDecision.name -> {
                val point = sharedPrefsHelper[PrefKey.USER_TRIP_COIN, ""]
                val newPoint = point.replace(",", "").toInt() + perWinCoin
                val userCoin = NumberFormat.getNumberInstance(Locale.US).format(newPoint)
                sharedPrefsHelper.put(PrefKey.USER_TRIP_COIN, userCoin)
            }

            ApiCallingKey.FlightPromotion.name -> {
                val response = (data.body as RestResponse<*>).response as FlightPromotion
                isFlightPromotionAvailable.set(true)
                promotionalImage.set(response.image)
            }
        }
    }

    override fun onAnyError(operationTag: String, errorMessage: String, type: ErrorType?) {
        when (operationTag) {
            ApiCallingKey.FlightPromotion.name -> {
                isFlightPromotionAvailable.set(false)
            }
        }
    }

    private fun fetchPopularTourCity() {
        val accessToken = sharedPrefsHelper[PrefKey.ACCESS_TOKEN, ""]

        executeSuspendedCodeBlock(ApiCallingKey.PopularTourCity.name) {
            DataManager.hotelApiService.getTravelReviewCities(accessToken)
        }
    }

    private fun generateTravelersAndRoomsInfo(): String {
        var guestCount = 0
        if (queryModel.rooms != null) {
            queryModel.rooms.forEach {
                guestCount += it.numberOfAdult + it.numberOfChildren
            }
            val roomCount = queryModel.rooms.size
            searchSummary.rooms = roomCount
            searchSummary.guest = guestCount
            return "$guestCount Guest(s) - $roomCount Room(s)"
        }
        return ""
    }

    fun onClickDestination() {
        navigateWithArgument(NavigationKey.Search.name, "")
    }

    fun onClickDateRange() {
        val startDateInMillisecond = getStartDateInMillisecond()
        val endDateInMillisecond = getEndDateInMillisecond()
        val startDateHintText = ""
        val endDateHintText = ""
        calenderData = CalenderData(
            startDateInMillisecond,
            endDateInMillisecond,
            startDateHintText,
            endDateHintText,
            serviceType = ServiceType.HOTEL.serviceName
        )
        navigateWithArgument(NavigationKey.Calender.name, calenderData)
    }

    fun onClickTravellers() {
        navigateWithArgument(NavigationKey.Travellers.name, queryModel.rooms)
    }

    fun onClickSearchHotel() {
        hotelEventManager.searchHotels()
        val summary = MoshiUtil.convertHotelSummaryToString(searchSummary)
        sharedPrefsHelper.put(PrefKey.HOTEL_SEARCH_SUMMARY, summary)
        sharedPrefsHelper.put(PrefKey.HOTEL_COUNT, 0)

        if (property != null)
            navigateWithArgument(NavigationKey.List.name, queryModel)
        else
            propertySelection.value = Event(false)
    }

    fun handleProperty(data: Intent) {
        property = data.getParcelableExtra(EXTRA_HOTEL_PROPERTY)
        queryModel.propertyCode = property?.id
        destination.set(property?.name)
        queryModel.propertyName = property?.name
    }

    fun handleRoomAndTravelers(data: Intent) {
        val rooms = data.getParcelableArrayListExtra<TravellerRoomInfo>(EXTRA_TRAVELER_ROOM_LIST)
        queryModel.rooms = rooms
        travellerAndRoomData.set(generateTravelersAndRoomsInfo())
    }

    fun handleCheckInCheckOutDate(calendar: Intent) {
        val startDateInMillisecond =
            calendar.getLongExtra(
                EXTRA_START_DATE_IN_MILLISECOND,
                Calendar.getInstance().timeInMillis
            )

        val endDateInMillisecond =
            calendar.getLongExtra(
                EXTRA_END_DATE_IN_MILLISECOND,
                Calendar.getInstance().timeInMillis
            )

        queryModel.checkin = DateUtil.parseApiDateFormatFromMillisecond(startDateInMillisecond)
        queryModel.checkout = DateUtil.parseApiDateFormatFromMillisecond(endDateInMillisecond)

        val startDate = DateUtil.parseDisplayDateFormatFromApiDateFormat(queryModel.checkin)
        val endDate = DateUtil.parseDisplayDateFormatFromApiDateFormat(queryModel.checkout)

        dateRangeData.set("$startDate - $endDate")
        searchSummary.dateRange = "$startDate - $endDate"
    }

    private fun getStartDateInMillisecond(): Long {
        val startDateInMillisecond: Long = try {
            val mStartDateString = queryModel.checkin
            DateUtil.parseDateToMillisecond(mStartDateString)
        } catch (e: ParseException) {
            Calendar.getInstance().timeInMillis
        }
        return startDateInMillisecond
    }

    private fun getEndDateInMillisecond(): Long {
        val endDateInMillisecond: Long = try {
            val mEndDateString = queryModel.checkout
            DateUtil.parseDateToMillisecond(mEndDateString)
        } catch (e: ParseException) {
            DateUtil.dayAfterTomorrowDateInMillisecond
        }
        return endDateInMillisecond
    }

    override fun selectWannaGoThere(cityCode: String) {
        hotelEventManager.playBeenThere()
        val decision = ReviewDecision(cityCode, "Yes")
        submitDecision(decision)
    }

    override fun selectHaveBeenThere(cityCode: String) {
        hotelEventManager.playBeenThere()
        val decision = ReviewDecision(cityCode, "No")
        submitDecision(decision)
    }

    override fun navigateToNext(position: Int) {
        if (position + 1 == liveCities.value?.size) showWishListContainer.set(false)
        scrollingPosition.value = position + 1
        this.position.set(position.plus(2).toString() + "/" + liveCities.value?.size)
    }

    private fun submitDecision(decision: ReviewDecision) {
        val accessToken = sharedPrefsHelper[PrefKey.ACCESS_TOKEN, ""]

        executeSuspendedCodeBlock(ApiCallingKey.SubmitDecision.name) {
            DataManager.hotelApiService.submitReviewDecision(accessToken, decision)
        }
    }

    private fun getFlightPromotion() {
        executeSuspendedCodeBlock(ApiCallingKey.FlightPromotion.name) {
            DataManager.hotelApiService.getFlightPromotionBanner()
        }
    }

    companion object {
        const val EXTRA_HOTEL_PROPERTY = "EXTRA_HOTEL_PROPERTY"
        const val ARG_CALENDER_DATA = "ARG_CALENDER_DATA"
    }
}
