package net.sharetrip.flight.history.view.list

import androidx.paging.Pager
import androidx.paging.PagingConfig
import androidx.paging.PagingData
import kotlinx.coroutines.flow.Flow
import net.sharetrip.flight.history.model.FlightHistoryResponse
import net.sharetrip.flight.network.FlightHistoryApiService

class FlightHistoryListRepo(private val apiService: FlightHistoryApiService) {

    fun getHistoryPagingData(token: String): Flow<PagingData<FlightHistoryResponse>> {
     return Pager(config = PagingConfig(
         pageSize = FlightHistoryListPagingSource.PAGE_SIZE,
         initialLoadSize = FlightHistoryListPagingSource.PAGE_SIZE * 2,
         enablePlaceholders = false
     ),
     pagingSourceFactory = { FlightHistoryListPagingSource(token, apiService)}).flow
    }
}