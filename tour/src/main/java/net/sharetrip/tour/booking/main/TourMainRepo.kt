package net.sharetrip.tour.booking.main

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.paging.Pager
import androidx.paging.PagingConfig
import net.sharetrip.tour.model.TourItem
import net.sharetrip.tour.network.TourBookingAPIService

class TourMainRepo(private val apiService: TourBookingAPIService) {

    private val _tourList = MutableLiveData<List<TourItem>>()
        val tourList: LiveData<List<TourItem>>
            get() = _tourList

     fun getTourList() =
        Pager(
            config = PagingConfig(
                pageSize = TourMainPagingSource.PAGE_SIZE,
                initialLoadSize = TourMainPagingSource.PAGE_SIZE * 2,
                enablePlaceholders = false
            ),
            pagingSourceFactory = { TourMainPagingSource(apiService) }
        ).flow

    suspend fun getPopularTourCity() = apiService.fetchPopularCityForTour()
}
