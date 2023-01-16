package net.sharetrip.visa.history.view.list

import androidx.lifecycle.MutableLiveData
import androidx.paging.PagingSource
import androidx.paging.PagingState
import com.sharetrip.base.network.NetworkState
import net.sharetrip.visa.history.model.VisaHistoryItem
import net.sharetrip.visa.network.VisaHistoryApiService
import retrofit2.HttpException
import java.io.IOException

class VisaHistoryPagingSource(
    private val token: String,
    private val apiService: VisaHistoryApiService,
    private val networkState: MutableLiveData<NetworkState>
) : PagingSource<Int, VisaHistoryItem>() {

    override fun getRefreshKey(state: PagingState<Int, VisaHistoryItem>): Int? =
        state.anchorPosition

    override suspend fun load(params: LoadParams<Int>): LoadResult<Int, VisaHistoryItem> {
        networkState.value = NetworkState.LOADING
        val position = params.key ?: STARTING_INDEX

        return try {
            val response = apiService.fetchVisaBookingHistoryList(token, PAGE_SIZE, position)
            val visaHistoryList = response.response.data

            if (position == STARTING_INDEX)
                if (visaHistoryList.isEmpty()) {
                    networkState.value = NetworkState.emptyResponse(null)
                } else {
                    networkState.value = NetworkState.LOADED
                }

            LoadResult.Page(
                data = visaHistoryList,
                prevKey = if (position == STARTING_INDEX) null else position.minus(10),
                nextKey = if (visaHistoryList.isEmpty()) null else position.plus(10)
            )
        } catch (e: IOException) {
            e.printStackTrace()
            if (params.key == STARTING_INDEX) networkState.value = NetworkState.error(e.message)
            LoadResult.Error(e)
        } catch (e: HttpException) {
            e.printStackTrace()
            if (params.key == STARTING_INDEX) networkState.value = NetworkState.error(e.message)
            LoadResult.Error(e)
        }
    }

    companion object {
        const val STARTING_INDEX = 0
        const val PAGE_SIZE = 10
    }
}
