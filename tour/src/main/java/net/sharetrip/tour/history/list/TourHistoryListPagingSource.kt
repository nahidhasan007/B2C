package net.sharetrip.tour.history.list

import androidx.paging.PagingSource
import androidx.paging.PagingState
import net.sharetrip.tour.booking.main.TourMainPagingSource
import net.sharetrip.tour.model.TourHistoryItem
import net.sharetrip.tour.network.TourHistoryAPIService
import retrofit2.HttpException
import java.io.IOException

class TourHistoryListPagingSource(
    private val token: String,
    private val apiService: TourHistoryAPIService
) : PagingSource<Int, TourHistoryItem>() {

    override fun getRefreshKey(state: PagingState<Int, TourHistoryItem>): Int? =
        state.anchorPosition

    override suspend fun load(params: LoadParams<Int>): LoadResult<Int, TourHistoryItem> {
        val position = params.key ?: STARTING_INDEX

        return try {
            val apiCall = apiService.getTourHistoryListResponse(
                token,
                PAGE_SIZE,
                position
            )

            LoadResult.Page(
                data = apiCall.response.data ?: listOf(),
                prevKey = if (position == TourMainPagingSource.STARTING_INDEX) null else position - 10,
                nextKey = if (apiCall.response.data.isNullOrEmpty()) null else position + 10
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
