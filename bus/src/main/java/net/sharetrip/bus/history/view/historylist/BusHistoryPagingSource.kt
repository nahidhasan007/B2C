package net.sharetrip.bus.history.view.historylist

import androidx.lifecycle.MutableLiveData
import androidx.paging.PagingSource
import androidx.paging.PagingState
import com.sharetrip.base.network.model.BaseResponse
import net.sharetrip.bus.history.model.HistoryDetails
import net.sharetrip.bus.network.BusHistoryApiService
import net.sharetrip.bus.utils.MsgUtils.networkErrorMsg
import net.sharetrip.bus.utils.MsgUtils.unKnownErrorMsg
import java.io.IOException

class BusHistoryPagingSource(val apiService: BusHistoryApiService, private val token: String) :
    PagingSource<Int, HistoryDetails>() {
    val isFirstPage = MutableLiveData(false)
    private val limit = 10

    override fun getRefreshKey(state: PagingState<Int, HistoryDetails>): Int? {
        return state.anchorPosition
    }

    override suspend fun load(params: LoadParams<Int>): LoadResult<Int, HistoryDetails> {
        var historyItems: List<HistoryDetails>? = null
        val position: Int = params.key ?: FIRST_PAGE_ITEM
        if (position == FIRST_PAGE_ITEM) isFirstPage.postValue(true)
        else isFirstPage.postValue(false)

        return when (val response =
            apiService.getBookingHistoryList(token, limit, position)) {
            is BaseResponse.Success -> {
                if (response.body.response?.history?.isNotEmpty() == true) {
                    historyItems = response.body.response!!.history
                    createPage(historyItems, position)
                } else {
                    if (position == FIRST_PAGE_ITEM) LoadResult.Error(Exception("No history found."))
                    else createPage(historyItems, position)
                }
            }
            is BaseResponse.ApiError -> LoadResult.Error(Exception(response.errorBody.message))

            is BaseResponse.NetworkError -> LoadResult.Error(IOException(networkErrorMsg))

            is BaseResponse.UnknownError -> LoadResult.Error(Exception(unKnownErrorMsg))
        }
    }

    private fun createPage(
        historyItems: List<HistoryDetails>?,
        position: Int,
    ) = LoadResult.Page(
        data = historyItems.orEmpty(),
        prevKey = if (position == FIRST_PAGE_ITEM) null else position - 1,
        nextKey = if (historyItems.isNullOrEmpty()) null else position + 1
    )

    companion object {
        const val FIRST_PAGE_ITEM = 0;
    }
}
