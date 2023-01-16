package net.sharetrip.visa.booking.view.home

import androidx.lifecycle.MutableLiveData
import androidx.paging.Pager
import androidx.paging.PagingConfig
import androidx.paging.PagingData
import kotlinx.coroutines.flow.Flow
import net.sharetrip.visa.booking.model.VisaItem
import net.sharetrip.visa.network.VisaBookingApiService

class VisaListRepository(val apiService: VisaBookingApiService, val nationality: String) {
    private val pageSize = 10
    val visaPagingSource = MutableLiveData<VisaPagingSource>()

    fun getVisaListFromRepository(): Flow<PagingData<VisaItem>> {
        val config = PagingConfig(
            pageSize = pageSize,
            prefetchDistance = pageSize,
            enablePlaceholders = false
        )

        val source =
            VisaPagingSource(
                nationality = nationality,
                apiService = apiService
            )

        visaPagingSource.postValue(source)

        return Pager(
            config = config,
            pagingSourceFactory = { source }
        ).flow
    }
}
