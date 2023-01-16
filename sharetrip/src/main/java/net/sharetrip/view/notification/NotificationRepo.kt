package net.sharetrip.view.notification

import androidx.lifecycle.LiveData
import androidx.paging.Pager
import androidx.paging.PagingConfig
import androidx.paging.PagingData
import androidx.paging.liveData
import net.sharetrip.shared.model.UserNotification
import net.sharetrip.network.MainApiService

class NotificationRepo(private val apiService: MainApiService) {

    fun getNotification(): LiveData<PagingData<UserNotification>> {
        return Pager(config = PagingConfig(
            pageSize = NotificationPagingSource.PAGE_SIZE,
            initialLoadSize = NotificationPagingSource.PAGE_SIZE * 2,
            enablePlaceholders = false
        ),
            pagingSourceFactory = { NotificationPagingSource(apiService) }).liveData
    }
}
