package net.sharetrip.bus.history.view.historylist

import androidx.lifecycle.MutableLiveData
import androidx.paging.Pager
import androidx.paging.PagingConfig
import androidx.paging.PagingData
import kotlinx.coroutines.flow.Flow
import net.sharetrip.bus.history.model.HistoryDetails
import net.sharetrip.bus.network.BusHistoryApiService

class BusHistoryListRepo(val apiService: BusHistoryApiService) {
    private val pageSize = 10
    val hotelHistoryPagingSource = MutableLiveData<BusHistoryPagingSource>()

    fun getFlightListFromRepository(token: String): Flow<PagingData<HistoryDetails>> {
        val config = PagingConfig(
            pageSize = pageSize,
            initialLoadSize = pageSize * 2,
            prefetchDistance = pageSize,
            enablePlaceholders = false
        )
        val source =
            BusHistoryPagingSource(
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
