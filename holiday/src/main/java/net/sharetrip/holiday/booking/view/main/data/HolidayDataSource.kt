package net.sharetrip.holiday.booking.view.main.data

import androidx.paging.PagingSource
import androidx.paging.PagingState
import net.sharetrip.holiday.booking.model.HolidayItem
import net.sharetrip.holiday.network.HolidayBookingApiService
import retrofit2.HttpException
import java.io.IOException

class HolidayDataSource(
    private val apiService: HolidayBookingApiService
) : PagingSource<Int, HolidayItem>() {

    override fun getRefreshKey(state: PagingState<Int, HolidayItem>): Int? = state.anchorPosition

    override suspend fun load(params: LoadParams<Int>): LoadResult<Int, HolidayItem> {
        val position = params.key ?: STARTING_INDEX

        return try {
            val holidayResponse = apiService.fetchPopularHoliday(PAGE_SIZE, position)
            val holidayList = holidayResponse.response.data as ArrayList

            for (holidayItem in holidayList) {
                if (holidayItem.image.isNotEmpty()) {
                    val shuffledImages = holidayItem.image.shuffled()
                    holidayItem.image.clear()
                    holidayItem.image.addAll(shuffledImages)
                }
            }

            if (holidayList.size < 10 && holidayList.isNotEmpty()) holidayList.add(
                holidayList.last()
            )

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

    companion object {
        const val STARTING_INDEX = 0
        const val PAGE_SIZE = 10
    }
}
