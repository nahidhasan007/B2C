package net.sharetrip.hotel.booking.view.hotellist

import androidx.lifecycle.MutableLiveData
import androidx.paging.PagingSource
import androidx.paging.PagingState
import com.google.gson.Gson
import com.sharetrip.base.data.PrefKey
import com.sharetrip.base.data.SharedPrefsHelper
import com.sharetrip.base.network.model.BaseResponse
import com.sharetrip.base.network.model.GenericResponse
import com.sharetrip.base.network.model.RestResponse
import io.socket.client.IO
import net.sharetrip.hotel.booking.model.*
import net.sharetrip.hotel.network.HotelBookingApiService
import net.sharetrip.hotel.utils.HotelSearchQuery
import net.sharetrip.hotel.utils.MsgUtils.networkErrorMsg
import net.sharetrip.hotel.utils.MsgUtils.unKnownErrorMsg
import org.json.JSONObject
import java.io.IOException

class HotelPagingSource(
    private val searchData: HotelSearchQuery,
    private val apiService: HotelBookingApiService,
    private val sharedPrefsHelper: SharedPrefsHelper,
    private val iHotelCountCallBack: IHotelListCallBack
) : PagingSource<Int, HotelInfo>() {
    private val isInitialLoad = MutableLiveData(true)

    override fun getRefreshKey(state: PagingState<Int, HotelInfo>): Int? {
        return state.anchorPosition?.let { anchorPosition ->
            state.closestPageToPosition(anchorPosition)?.prevKey?.plus(10)
                ?: state.closestPageToPosition(anchorPosition)?.nextKey?.minus(10)
        }
    }

    override suspend fun load(params: LoadParams<Int>): LoadResult<Int, HotelInfo> {
        var hotelResponseList: List<HotelInfo>? = null
        val position: Int = params.key ?: FIRST_PAGE_ITEM

        if (position == FIRST_PAGE_ITEM) {
            iHotelCountCallBack.isFirstPage(true)
        } else {
            iHotelCountCallBack.isFirstPage(false)
        }

        val queryMap = generateQueryMap(searchData, position)
        val response: GenericResponse<RestResponse<HotelResponse>> =
            apiService.getHotelSearchResponse(queryMap)

        return when (response) {
            is BaseResponse.Success -> {
                if (response.body.response.hotelList.isNotEmpty()) {
                    val hotelResponse = response.body.response

                    if (position == FIRST_PAGE_ITEM && isInitialLoad.value!!) {
                        iHotelCountCallBack.searchCode(response.body.response.searchCode)
                        isInitialLoad.value = false
                        val progressRangeDummy = PriceRangeDumy(
                            hotelResponse.priceRange.low,
                            hotelResponse.priceRange.high,
                            hotelResponse.priceRange.low,
                            hotelResponse.priceRange.high
                        )
                        hotelResponse.filter.priceRange = progressRangeDummy
                        hotelResponse.filter.locationRange = LocationRange(25, 25)
                        val ratingList = ArrayList<PropertyRating>()

                        for (i in 1..5) {
                            ratingList.add(PropertyRating(i))
                        }

                        hotelResponse.filter.ratingList = ratingList
                        hotelResponse.filter.searchHotel = SearchHotel("")
                        socketSetup(hotelResponse.searchCode)
                    }

                    if (position == FIRST_PAGE_ITEM) {
                        iHotelCountCallBack.hotelCount(hotelResponse.totalHotels)
                        iHotelCountCallBack.filter(hotelResponse.filter)
                    }

                    hotelResponseList = hotelResponse.hotelList
                    createPage(hotelResponseList, position)
                } else {
                    if (position == FIRST_PAGE_ITEM) LoadResult.Error(Exception(noFlightFoundMsg))
                    else createPage(hotelResponseList, position)
                }
            }
            is BaseResponse.ApiError -> LoadResult.Error(Exception(response.errorBody.message))

            is BaseResponse.NetworkError -> LoadResult.Error(IOException(networkErrorMsg))

            is BaseResponse.UnknownError -> LoadResult.Error(Exception(unKnownErrorMsg))
        }
    }

    private fun createPage(
        flightSearchResponse: List<HotelInfo>?,
        position: Int
    ) = LoadResult.Page(
        data = flightSearchResponse.orEmpty(),
        prevKey = if (position == FIRST_PAGE_ITEM) null else position - 10,
        nextKey = if (flightSearchResponse.isNullOrEmpty()) null else position + 10
    )

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

    private fun socketSetup(searchCode: String) {
        val opts = IO.Options()
        opts.forceNew = true
        opts.reconnection = false
        opts.transports = arrayOf("websocket")

        val socket = IO.socket("https://socket.sharetrip.net/hotel", opts)
        var count: Int

        socket.on(searchCode) {
            try {
                val jsonObject = it[0] as JSONObject
                iHotelCountCallBack.hotelCount(jsonObject.getInt("count"))
                count = jsonObject.getInt("count")
                if (sharedPrefsHelper[PrefKey.HOTEL_COUNT, 0] == 0) {
                    sharedPrefsHelper.put(PrefKey.HOTEL_COUNT, count)
                } else if (count > sharedPrefsHelper[PrefKey.HOTEL_COUNT, 0]) {
                    sharedPrefsHelper.put(PrefKey.HOTEL_COUNT, count)
                    iHotelCountCallBack.hotelCountServer(count)
                }
            } catch (e: Exception) {
            }

        }

        socket.connect()
    }

    companion object {
        private const val FIRST_PAGE_ITEM = 0
        const val noFlightFoundMsg =
            "Dear Valued Customer, we do not have any hotel inventory for your desired destination."
    }
}
