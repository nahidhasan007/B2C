package net.sharetrip.flight.booking.view.flightlist

import androidx.lifecycle.MutableLiveData
import androidx.paging.PagingSource
import androidx.paging.PagingState
import com.sharetrip.base.network.model.BaseResponse
import com.sharetrip.base.network.model.GenericResponse
import com.sharetrip.base.network.model.RestResponse
import net.sharetrip.flight.booking.model.FlightSearch
import net.sharetrip.flight.booking.model.filter.Airline
import net.sharetrip.flight.booking.model.filter.FilterParams
import net.sharetrip.flight.booking.model.filter.FlightFilter
import net.sharetrip.flight.booking.model.flightresponse.FlightsResponse
import net.sharetrip.flight.booking.model.flightresponse.flights.Flights
import net.sharetrip.flight.booking.model.flightresponse.flights.filter.FilterData
import net.sharetrip.flight.booking.view.flightlist.FlightListRepository.Companion.PAGE_SIZE
import net.sharetrip.flight.network.FlightBookingApiService
import net.sharetrip.flight.utils.MsgUtils.networkErrorMsg
import net.sharetrip.flight.utils.MsgUtils.unKnownErrorMsg
import java.io.IOException
import java.util.*

class FlightPagingSource(
    private val flightSearch: FlightSearch, var filterData: FilterData,
    private val apiService: FlightBookingApiService,
) : PagingSource<Int, Flights>() {

    private val totalRecord = MutableLiveData<Int>()
    private val filterAirlinesList = MutableLiveData<List<Airline>>()
    private var filter: FlightFilter? = null

    val searchId = MutableLiveData<String>()
    val sessionId = MutableLiveData<String>()
    val filterDeal = MutableLiveData<String>()
    val isInitialLoad = MutableLiveData(true)
    val isFirstPage = MutableLiveData(false)

    override fun getRefreshKey(state: PagingState<Int, Flights>): Int? {
        return state.anchorPosition
    }

    override suspend fun load(params: LoadParams<Int>): LoadResult<Int, Flights> {
        var flightSearchResponse: List<Flights>? = null
        val position: Int = params.key ?: FIRST_PAGE_ITEM
        if (position == FIRST_PAGE_ITEM) isFirstPage.postValue(true)
        else isFirstPage.postValue(false)

        val response: GenericResponse<RestResponse<FlightsResponse>> =
            if (filterData.filter == null && position == FIRST_PAGE_ITEM) {
                apiService.getFlights(
                    flightSearch.tripType,
                    flightSearch.adult,
                    flightSearch.child,
                    flightSearch.childBirthListOnly,
                    flightSearch.infant,
                    flightSearch.classType,
                    flightSearch.origin,
                    flightSearch.destination,
                    flightSearch.depart
                )
            } else {
                if (searchId.value != null) filterData.searchId = searchId.value!!
                filterData.page = position
                if (filterData.filter == null) filterData.filter = FilterParams()
                apiService.getFlightFilter(filterData)
            }

        return when (response) {
            is BaseResponse.Success -> {
                if (response.body.response?.flights?.isNotEmpty() == true) {
                    flightSearchResponse = response.body.response!!.flights
                    if (position == 1) {
                        filterAirlinesList.postValue(response.body.response!!.filters.airlines)
                        val (_, _, _, _, totalRecords, _, filterDeal_data, _, filters, _) = response.body.response!!
                        totalRecord.postValue(totalRecords)
                        filterDeal.postValue(filterDeal_data.uppercase(Locale.getDefault()))
                        if (isInitialLoad.value!!) {
                            searchId.value = response.body.response!!.searchId
                            sessionId.value = response.body.response!!.sessionId
                            isInitialLoad.value = false
                            filterData.searchId = response.body.response!!.searchId
                        }
                        filter = filters
                    }
                    createPage(flightSearchResponse, position)
                } else {
                    if (position == FIRST_PAGE_ITEM) LoadResult.Error(Exception(noFlightFoundMsg))
                    else createPage(flightSearchResponse, position)
                }
            }
            is BaseResponse.ApiError -> LoadResult.Error(Exception(response.errorBody.message))

            is BaseResponse.NetworkError -> LoadResult.Error(IOException(networkErrorMsg))

            is BaseResponse.UnknownError -> LoadResult.Error(Exception(unKnownErrorMsg))
        }
    }

    private fun createPage(
        flightSearchResponse: List<Flights>?,
        position: Int,
    ) = LoadResult.Page(
        data = flightSearchResponse.orEmpty(),
        prevKey = if (position == FIRST_PAGE_ITEM) null else position - 1,
        nextKey = if (flightSearchResponse.isNullOrEmpty() || flightSearchResponse.size < PAGE_SIZE) null else position + 1
    )

    fun totalRecord(): MutableLiveData<Int> {
        return totalRecord
    }

    fun filterAirlinesList(): MutableLiveData<List<Airline>> {
        return filterAirlinesList
    }

    fun flightFilter(): FlightFilter? {
        return filter
    }

    companion object {
        private const val FIRST_PAGE_ITEM = 1
        const val noFlightFoundMsg =
            "We've searched more than 100 airlines that we sell, and couldn't find any flights on these dates. Please try Changing your search details."
    }
}
