package net.sharetrip.holiday.booking.view.list

import androidx.databinding.ObservableField
import androidx.paging.Pager
import androidx.paging.PagingConfig
import androidx.paging.PagingData
import kotlinx.coroutines.flow.Flow
import net.sharetrip.holiday.booking.model.HolidayItem
import net.sharetrip.holiday.booking.view.list.data.HolidayDataSource
import net.sharetrip.holiday.network.HolidayBookingApiService

class HolidayListRepository(private val apiService: HolidayBookingApiService) {

    fun getHolidaysLiveData(cityCode: String,  tripCount: ObservableField<String>): Flow<PagingData<HolidayItem>> {
        return Pager(config = PagingConfig(
            pageSize = HolidayDataSource.PAGE_SIZE,
            initialLoadSize = HolidayDataSource.PAGE_SIZE * 2,
            enablePlaceholders = false,
        ),
            pagingSourceFactory = { HolidayDataSource(cityCode, apiService, tripCount)}
        ).flow
    }
}