package net.sharetrip.holiday.history.view.list

import androidx.lifecycle.MutableLiveData
import androidx.paging.PagingSource
import androidx.paging.PagingState
import com.sharetrip.base.network.NetworkState
import net.sharetrip.holiday.history.model.HolidayHistoryItem
import net.sharetrip.holiday.network.HolidayHistoryApiService
import retrofit2.HttpException
import java.io.IOException

class HolidayHistoryPagingSource(
    private val token: String,
    private val apiService: HolidayHistoryApiService
) : PagingSource<Int, HolidayHistoryItem>() {

    val networkState = MutableLiveData<NetworkState>()

    override fun getRefreshKey(state: PagingState<Int, HolidayHistoryItem>): Int? {
        return state.anchorPosition
    }

    override suspend fun load(params: LoadParams<Int>): LoadResult<Int, HolidayHistoryItem> {
        val position = params.key ?: STARTING_INDEX

        return try {
            val responseData = apiService.getHolidayHistoryResponse(token, PAGE_SIZE, position)

            if (responseData.response.data.isEmpty() && position == STARTING_INDEX) {
                networkState.postValue(NetworkState.emptyResponse(null))
            } else {
                networkState.postValue(NetworkState.LOADED)
            }

            LoadResult.Page(
                data = responseData.response.data,
                prevKey = if (position == STARTING_INDEX) null else position - 10,
                nextKey = if (responseData.response.data.isNullOrEmpty()) null else position + 10
            )
        } catch (e: IOException) {
            e.printStackTrace()
            networkState.postValue(NetworkState.error(e.message))
            LoadResult.Error(e)
        }catch (e: HttpException){
            e.printStackTrace()
            networkState.postValue(NetworkState.error(e.message))
            LoadResult.Error(e)
        }
    }

    companion object {
        const val STARTING_INDEX = 0
        const val PAGE_SIZE = 10
    }
}
