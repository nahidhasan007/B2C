package net.sharetrip.tour.booking.list

import androidx.paging.PagingSource
import androidx.paging.PagingState
import com.sharetrip.base.network.model.BaseResponse
import net.sharetrip.tour.booking.main.TourMainPagingSource
import net.sharetrip.tour.model.TourItem
import net.sharetrip.tour.network.TourBookingAPIService
import retrofit2.HttpException
import java.io.IOException

class TourListPagingSource(
    private val cityCode: String,
    private val apiService: TourBookingAPIService
) : PagingSource<Int, TourItem>() {

    override fun getRefreshKey(state: PagingState<Int, TourItem>): Int? = state.anchorPosition

    override suspend fun load(params: LoadParams<Int>): LoadResult<Int, TourItem> {
        val position = params.key ?: STARTING_INDEX

        return try {
            val apiCall = apiService.fetchTourList(
                TourMainPagingSource.PAGE_SIZE,
                position,
                cityCode = "\"$cityCode\""
            )
            if (apiCall is BaseResponse.Success) {
                val tourResponse = apiCall.body
                val tourList = tourResponse.response.data as ArrayList

                LoadResult.Page(
                    data = tourList,
                    prevKey = if (position == TourMainPagingSource.STARTING_INDEX) null else position - 10,
                    nextKey = if (tourList.isNullOrEmpty()) null else position + 10
                )
            } else {
                LoadResult.Page(
                    data = emptyList(),
                    prevKey = null,
                    nextKey = null
                )
            }
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
