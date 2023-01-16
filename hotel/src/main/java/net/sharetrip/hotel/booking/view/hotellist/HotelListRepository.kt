package net.sharetrip.hotel.booking.view.hotellist

import androidx.paging.Pager
import androidx.paging.PagingConfig
import androidx.paging.PagingData
import com.sharetrip.base.data.SharedPrefsHelper
import kotlinx.coroutines.flow.Flow
import net.sharetrip.hotel.booking.model.HotelInfo
import net.sharetrip.hotel.network.HotelBookingApiService
import net.sharetrip.hotel.utils.HotelSearchQuery

class HotelListRepository(
    private val apiService: HotelBookingApiService,
    private val sharedPrefsHelper: SharedPrefsHelper,
    private val iHotelListCallBack: IHotelListCallBack
) {
    fun getHotelListFromRepository(searchData: HotelSearchQuery): Flow<PagingData<HotelInfo>> =
        Pager(
            config = PagingConfig(
                pageSize = PAGE_SIZE,
                initialLoadSize = PAGE_SIZE * 2,
                enablePlaceholders = false
            ),
            pagingSourceFactory = {
                HotelPagingSource(
                    searchData = searchData,
                    apiService = apiService,
                    sharedPrefsHelper = sharedPrefsHelper,
                    iHotelCountCallBack = iHotelListCallBack
                )
            }
        ).flow

    companion object {
        const val PAGE_SIZE = 10
    }
}
