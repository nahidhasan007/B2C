package net.sharetrip.tour.booking.list

import androidx.paging.Pager
import androidx.paging.PagingConfig
import androidx.paging.liveData
import net.sharetrip.tour.network.TourBookingAPIService

class TourListRepo(private val apiService: TourBookingAPIService) {

     fun getTourList(cityCode: String) =
        Pager(
            config = PagingConfig(
                pageSize = TourListPagingSource.PAGE_SIZE,
                initialLoadSize = TourListPagingSource.PAGE_SIZE * 2,
                enablePlaceholders = false
            ),
            pagingSourceFactory = { TourListPagingSource(cityCode, apiService) }

        ).liveData
}
