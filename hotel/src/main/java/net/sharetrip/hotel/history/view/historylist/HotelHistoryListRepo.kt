package net.sharetrip.hotel.history.view.historylist

import androidx.lifecycle.MutableLiveData
import androidx.paging.Pager
import androidx.paging.PagingConfig
import androidx.paging.PagingData
import kotlinx.coroutines.flow.Flow
import net.sharetrip.hotel.history.model.HotelHistoryItem
import net.sharetrip.hotel.network.HotelHistoryApiService

class HotelHistoryListRepo(val apiService: HotelHistoryApiService) {
    private val PAGE_SIZE = 10
    val hotelHistoryPagingSource = MutableLiveData<HotelHistoryPagingSource>()

    fun getFlightListFromRepository(token: String): Flow<PagingData<HotelHistoryItem>> {
        val config = PagingConfig(
            pageSize = PAGE_SIZE,
            initialLoadSize = PAGE_SIZE * 2,
            prefetchDistance = PAGE_SIZE,
            enablePlaceholders = false
        )
        val source =
            HotelHistoryPagingSource(
                token = token,
                apiService = apiService
            )

        hotelHistoryPagingSource.postValue(source)

        return Pager(
            config = config,
            pagingSourceFactory = { source }
        ).flow
    }
}
