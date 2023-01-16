package net.sharetrip.flight.booking.view.flightlist

import androidx.lifecycle.MutableLiveData
import androidx.paging.Pager
import androidx.paging.PagingConfig
import androidx.paging.PagingData
import kotlinx.coroutines.flow.Flow
import net.sharetrip.flight.booking.model.FlightSearch
import net.sharetrip.flight.booking.model.flightresponse.flights.Flights
import net.sharetrip.flight.booking.model.flightresponse.flights.filter.FilterData
import net.sharetrip.flight.network.FlightBookingApiService

class FlightListRepository(
    val flightSearch: FlightSearch,
    val apiService: FlightBookingApiService,
) {
    val flightPagingSource = MutableLiveData<FlightPagingSource>()

    fun getFlightListFromRepository(filterData: FilterData): Flow<PagingData<Flights>> {
        val config = PagingConfig(
            pageSize = PAGE_SIZE,
            initialLoadSize = PAGE_SIZE,
            prefetchDistance = PAGE_SIZE,
            enablePlaceholders = false
        )
        val source =
            FlightPagingSource(
                flightSearch = flightSearch,
                filterData = filterData,
                apiService = apiService
            )

        flightPagingSource.postValue(source)

        return Pager(
            config = config,
            pagingSourceFactory = { source }
        ).flow
    }

    companion object {
        const val PAGE_SIZE = 10
    }
}
