package net.sharetrip.flight.history.view.list

import androidx.paging.PagingSource
import androidx.paging.PagingState
import com.sharetrip.base.network.model.BaseResponse
import com.sharetrip.base.network.model.RestResponse
import net.sharetrip.flight.history.model.FlightHistoryResponse
import net.sharetrip.flight.network.FlightHistoryApiService
import retrofit2.HttpException
import java.io.IOException

class FlightHistoryListPagingSource(
    private val token: String,
    private val apiService: FlightHistoryApiService
) : PagingSource<Int, FlightHistoryResponse>() {

    override fun getRefreshKey(state: PagingState<Int, FlightHistoryResponse>): Int? {
        return state.anchorPosition
    }

    override suspend fun load(params: LoadParams<Int>): LoadResult<Int, FlightHistoryResponse> {
        val position = params.key ?: STARTING_INDEX
        return try {
            val responseData = apiService.getFlightHistoryResponse(token, PAGE_SIZE, position).response.data
            LoadResult.Page(
                data = responseData,
                prevKey = if (position == STARTING_INDEX) null else position - 10,
                nextKey = if (responseData.isNullOrEmpty()) null else position + 10
            )
        }catch (e: IOException){
            e.printStackTrace()
            LoadResult.Error(e)
        }catch (e: HttpException){
            e.printStackTrace()
            LoadResult.Error(e)
        }
    }

    companion object {
        const val STARTING_INDEX = 0
        const val PAGE_SIZE = 10
    }
}