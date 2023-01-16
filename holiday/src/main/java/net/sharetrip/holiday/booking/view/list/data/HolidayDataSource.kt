package net.sharetrip.holiday.booking.view.list.data

import androidx.databinding.ObservableField
import androidx.paging.PagingSource
import androidx.paging.PagingState
import net.sharetrip.holiday.booking.model.HolidayItem
import net.sharetrip.holiday.network.HolidayBookingApiService
import retrofit2.HttpException
import java.io.IOException

class HolidayDataSource(
    private val cityCode: String, private val apiService: HolidayBookingApiService,
    private val tripCount: ObservableField<String>
) : PagingSource<Int, HolidayItem>() {

    override suspend fun load(params: LoadParams<Int>): LoadResult<Int, HolidayItem> {
        val position = params.key ?: STARTING_INDEX

        return try {
            val holidayResponse =
                apiService.fetchCityHoliday("[$cityCode]", PAGE_SIZE, position)
            val holidayList = holidayResponse.response.data

            if (position == STARTING_INDEX)
                if (holidayResponse.response.count == 0) {
                    tripCount.set("No Available Holiday")
                } else {
                    tripCount.set(holidayResponse.response.count.toString() + " Available Holiday")
                }

            for (holidayItem in holidayList) {
                if (holidayItem.image.isNotEmpty()) {
                    val shuffledImages = holidayItem.image.shuffled()
                    holidayItem.image.clear()
                    holidayItem.image.addAll(shuffledImages)
                }
            }

            LoadResult.Page(
                data = holidayList,
                prevKey = if (position == STARTING_INDEX) null else position - 10,
                nextKey = if (holidayList.isNullOrEmpty()) null else position + 10
            )

        } catch (e: IOException) {
            e.printStackTrace()
            LoadResult.Error(e)
        } catch (e: HttpException) {
            e.printStackTrace()
            LoadResult.Error(e)
        }
    }

    override fun getRefreshKey(state: PagingState<Int, HolidayItem>): Int? {
        return state.anchorPosition
    }

    companion object {
        const val STARTING_INDEX = 0
        const val PAGE_SIZE = 10
    }
}
