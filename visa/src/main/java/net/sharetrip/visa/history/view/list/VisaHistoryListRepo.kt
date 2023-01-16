package net.sharetrip.visa.history.view.list

import androidx.lifecycle.MutableLiveData
import androidx.paging.Pager
import androidx.paging.PagingConfig
import androidx.paging.PagingData
import com.sharetrip.base.network.NetworkState
import kotlinx.coroutines.flow.Flow
import net.sharetrip.visa.history.model.VisaHistoryItem
import net.sharetrip.visa.history.view.list.VisaHistoryPagingSource.Companion.PAGE_SIZE
import net.sharetrip.visa.network.VisaHistoryApiService

class VisaHistoryListRepo(private val apiService: VisaHistoryApiService) {

    fun getVisaHistoryList(
        token: String,
        networkState: MutableLiveData<NetworkState>
    ): Flow<PagingData<VisaHistoryItem>> {
        return Pager(
            config = PagingConfig(
                pageSize = PAGE_SIZE,
                initialLoadSize = PAGE_SIZE * 2,
                enablePlaceholders = false
            ),
            pagingSourceFactory = { VisaHistoryPagingSource(token, apiService, networkState) }
        ).flow
    }
}
