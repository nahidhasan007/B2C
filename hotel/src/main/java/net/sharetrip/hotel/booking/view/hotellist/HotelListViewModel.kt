package net.sharetrip.hotel.booking.view.hotellist

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.viewModelScope
import androidx.paging.PagingData
import androidx.paging.cachedIn
import com.google.gson.Gson
import com.sharetrip.base.data.SharedPrefsHelper
import com.sharetrip.base.viewmodel.BaseViewModel
import kotlinx.coroutines.delay
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.flatMapLatest
import net.sharetrip.hotel.booking.model.Filter
import net.sharetrip.hotel.booking.model.HotelFilterData
import net.sharetrip.hotel.booking.model.HotelInfo
import net.sharetrip.hotel.network.HotelBookingApiService
import net.sharetrip.hotel.utils.HotelSearchQuery
import net.sharetrip.hotel.utils.MoshiUtil.convertListParamToIntString
import net.sharetrip.hotel.utils.MoshiUtil.convertListParamToString
import net.sharetrip.hotel.utils.SingleLiveEvent
import net.sharetrip.shared.utils.DateFormatPattern
import net.sharetrip.shared.utils.DateUtil
import net.sharetrip.shared.utils.analytics.AnalyticsProvider

class HotelListViewModel(
    private var searchData: HotelSearchQuery,
    val apiService: HotelBookingApiService,
    sharedPrefsHelper: SharedPrefsHelper
) : BaseViewModel(), IHotelListCallBack {
    val dateRange: String
    var guestCount = 0
    val roomCount: Int
    val navigateToHotelFilter = SingleLiveEvent<Any>()
    val navigateBack = SingleLiveEvent<Any>()
    private val filterData = MutableStateFlow(searchData)

    private val hotelListRepository: HotelListRepository =
        HotelListRepository(apiService, sharedPrefsHelper, this)

    val hotelCount: LiveData<Int>
        get() = mutableHotelCount
    private val mutableHotelCount = MutableLiveData<Int>()

    val hotelCountServer: LiveData<Int>
        get() = mutableHotelCountServer
    private val mutableHotelCountServer = MutableLiveData<Int>()

    val searchCode: LiveData<String>
        get() = mutableSearchCode
    private val mutableSearchCode = MutableLiveData<String>()

    val filter: LiveData<Filter>
        get() = mutableFilter
    private val mutableFilter = MutableLiveData<Filter>()

    val isFirstPage: LiveData<Boolean>
        get() = mutableIsFirstPage
    private val mutableIsFirstPage = MutableLiveData<Boolean>()

    val hotelDataList: Flow<PagingData<HotelInfo>> = filterData.flatMapLatest {
        // Hotel API doesn't give proper values first time, so adding extra api call with delay
        if (isFirstPage.value == true || isFirstPage.value == null) {
            getFirstSearch()
            delay(3000)
        }
        hotelListRepository.getHotelListFromRepository(it).cachedIn(viewModelScope)
    }

    val hotelEventManager =
        AnalyticsProvider.hotelEventManager(AnalyticsProvider.getFirebaseAnalytics())

    init {
        val startDate = DateUtil.revampingDateFormatFromCurrentToGiven(
            searchData.checkin,
            DateFormatPattern.API_DATE_PATTERN,
            DateFormatPattern.DISPLAY_DATE_PATTERN_FOR_HOTEL
        )
        val endDate = DateUtil.revampingDateFormatFromCurrentToGiven(
            searchData.checkout,
            DateFormatPattern.API_DATE_PATTERN,
            DateFormatPattern.DISPLAY_DATE_PATTERN_FOR_HOTEL
        )

        dateRange = "$startDate - $endDate"
        searchData.rooms.forEach {
            guestCount += it.numberOfAdult + it.numberOfChildren
        }
        roomCount = searchData.rooms.size
        filterData.value = searchData
    }

    override fun hotelCount(count: Int) {
        mutableHotelCount.postValue(count)
    }

    override fun hotelCountServer(count: Int) {
        mutableHotelCountServer.postValue(count)
    }

    override fun searchCode(searchId: String) {
        mutableSearchCode.postValue(searchId)
    }

    override fun filter(filter: Filter) {
        mutableFilter.postValue(filter)
    }

    override fun isFirstPage(value: Boolean) {
        mutableIsFirstPage.postValue(value)
    }

    fun handleFilterSearchData(data: HotelFilterData) {
        searchData.priceRange = data.priceRange
        searchData.propertyRating = convertListParamToIntString(data.propertyRating)
        searchData.meals = convertListParamToString(data.meals)
        searchData.propertyType = convertListParamToString(data.propertyType)
        searchData.amenities = convertListParamToString(data.amenities)
        searchData.hotelName = data.hotelName
        searchData.distance = data.distance
        searchData.pointofinterest = convertListParamToString(data.pointOfInterests)
        searchData.neighborhoods = convertListParamToString(data.neighborhoods)
        filterData.value = searchData
    }

    fun onClickFilter() {
        hotelEventManager.clickOnHotelFilter()
        navigateToHotelFilter.call()
    }

    fun onButtonRetry() {
        navigateBack.call()
    }

    fun onDestroyCall() {
        searchData.priceRange = null
        searchData.propertyRating = ""
        searchData.meals = ""
        searchData.propertyType = ""
        searchData.amenities = ""
        searchData.hotelName = ""
        searchData.distance = ""
        searchData.pointofinterest = ""
        searchData.neighborhoods = ""
    }

    private suspend fun getFirstSearch() {
        apiService.getHotelSearchResponse(generateQueryMap(filterData.value, 0))
    }

    private fun generateQueryMap(
        searchData: HotelSearchQuery,
        position: Int
    ): MutableMap<String, String> {
        val queryMap = mutableMapOf(
            "limit" to "10",
            "currency" to "BDT",
            "propertyCode" to searchData.propertyCode,
            "checkin" to searchData.checkin,
            "checkout" to searchData.checkout,
            "nationality" to "bd",
            "rooms" to searchData.roomsJsonStringData,
            "offset" to position.toString()
        )

        queryMap.apply {
            if (searchData.priceRange != null) queryMap["priceRange"] =
                Gson().toJson(searchData.priceRange)
            if (searchData.hotelName != "") queryMap["hotelName"] = searchData.hotelName
            if (searchData.distance != "") queryMap["distance"] = searchData.distance
            if (searchData.propertyRating != null && searchData.propertyRating.isNotEmpty()) queryMap["starRating"] =
                searchData.propertyRating
            if (searchData.propertyType != null && searchData.propertyType.isNotEmpty()) queryMap["propertyType"] =
                searchData.propertyType
            if (searchData.meals != null && searchData.meals.isNotEmpty()) queryMap["meals"] =
                searchData.meals
            if (searchData.amenities != null && searchData.amenities.isNotEmpty()) queryMap["amenities"] =
                searchData.amenities
            if (searchData.pointofinterest != null && searchData.pointofinterest.isNotEmpty()) queryMap["point_of_interest"] =
                searchData.pointofinterest
            if (searchData.neighborhoods != null && searchData.neighborhoods.isNotEmpty()) queryMap["neighborhood"] =
                searchData.neighborhoods
        }

        return queryMap
    }
}
