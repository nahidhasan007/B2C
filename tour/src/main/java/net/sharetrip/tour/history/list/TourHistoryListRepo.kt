package net.sharetrip.tour.history.list

import androidx.paging.Pager
import androidx.paging.PagingConfig
import androidx.paging.liveData
import net.sharetrip.tour.booking.list.TourListPagingSource
import net.sharetrip.tour.network.TourHistoryAPIService

class TourHistoryListRepo(private val apiService: TourHistoryAPIService) {

    fun getTourList(token: String) =
        Pager(
            config = PagingConfig(
                pageSize = TourHistoryListPagingSource.PAGE_SIZE,
                initialLoadSize = TourHistoryListPagingSource.PAGE_SIZE * 2,
                enablePlaceholders = false
            ),
            pagingSourceFactory = { TourHistoryListPagingSource(token, apiService) }

        ).liveData
}
