package net.sharetrip.holiday.history.view.list

import androidx.lifecycle.MutableLiveData
import androidx.paging.Pager
import androidx.paging.PagingConfig
import androidx.paging.PagingData
import kotlinx.coroutines.flow.Flow
import net.sharetrip.holiday.history.model.HolidayHistoryItem
import net.sharetrip.holiday.network.HolidayHistoryApiService

class HolidayHistoryListRepo(private val apiService: HolidayHistoryApiService) {

    val holidayHistoryPagingSource = MutableLiveData<HolidayHistoryPagingSource>()

    fun getHistoryListData(token: String): Flow<PagingData<HolidayHistoryItem>> {
        val source = HolidayHistoryPagingSource(token, apiService)
        holidayHistoryPagingSource.postValue(source)
        return Pager(
            config = PagingConfig(
                pageSize = HolidayHistoryPagingSource.PAGE_SIZE,
                initialLoadSize = HolidayHistoryPagingSource.PAGE_SIZE * 2,
                enablePlaceholders = false
            ),
            pagingSourceFactory = {source}
        ).flow
    }
}