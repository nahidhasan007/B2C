package net.sharetrip.tour.booking.main

import androidx.paging.PagingSource
import androidx.paging.PagingState
import com.sharetrip.base.network.model.BaseResponse
import net.sharetrip.tour.model.TourItem
import net.sharetrip.tour.network.TourBookingAPIService
import retrofit2.HttpException
import java.io.IOException

class TourMainPagingSource(private val apiService: TourBookingAPIService) :
    PagingSource<Int, TourItem>() {

    override fun getRefreshKey(state: PagingState<Int, TourItem>): Int? {
        return state.anchorPosition
    }

    override suspend fun load(params: LoadParams<Int>): LoadResult<Int, TourItem> {
        val position = params.key ?: STARTING_INDEX

        return try {
            val apiCall = apiService.fetchTourList(PAGE_SIZE, position)
            val tourResponse = (apiCall as BaseResponse.Success).body
            val tourList = tourResponse.response.data as ArrayList
            if (tourList.size < 10 && tourList.isNotEmpty()) tourList.add(tourList.last())

            LoadResult.Page(
                data = tourList,
                prevKey = if (position == STARTING_INDEX) null else position - 10,
                nextKey = if (tourList.isNullOrEmpty()) null else position + 10
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
