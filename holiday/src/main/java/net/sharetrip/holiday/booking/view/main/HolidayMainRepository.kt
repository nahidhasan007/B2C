package net.sharetrip.holiday.booking.view.main

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.paging.Pager
import androidx.paging.PagingConfig
import com.sharetrip.base.event.Event
import net.sharetrip.holiday.booking.model.HolidayCity
import net.sharetrip.holiday.booking.view.main.data.HolidayDataSource
import net.sharetrip.holiday.network.HolidayBookingApiService

class HolidayMainRepository(private val apiService: HolidayBookingApiService) {

    private val _liveCities = MutableLiveData<List<HolidayCity>>()
    val liveCities: LiveData<List<HolidayCity>>
        get() = _liveCities

    val showToastMessage = MutableLiveData<Event<String>>()

    suspend fun getPopularCityForHoliday() {
        try {
            val data = apiService.fetchPopularCityForHoliday()
            _liveCities.value = data.response!!
        } catch (e: Exception) {
            e.message
            showToastMessage.value = Event(e.message.toString())
        }
    }

    fun getPopularHolidays() =
        Pager(
            config = PagingConfig(
                pageSize = HolidayDataSource.PAGE_SIZE,
                initialLoadSize = HolidayDataSource.PAGE_SIZE * 2,
                enablePlaceholders = false
            ),
            pagingSourceFactory = { HolidayDataSource(apiService) }
        ).flow

}