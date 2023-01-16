package net.sharetrip.hotel.history.view.historylist

import androidx.lifecycle.MutableLiveData
import androidx.paging.PagingSource
import androidx.paging.PagingState
import com.sharetrip.base.network.model.BaseResponse
import net.sharetrip.hotel.history.model.HotelHistoryItem
import net.sharetrip.hotel.network.HotelHistoryApiService
import net.sharetrip.hotel.utils.MsgUtils.networkErrorMsg
import net.sharetrip.hotel.utils.MsgUtils.unKnownErrorMsg
import java.io.IOException

class HotelHistoryPagingSource(val apiService: HotelHistoryApiService, private val token: String) :
    PagingSource<Int, HotelHistoryItem>() {
    val isFirstPage = MutableLiveData(false)
    private val limit = 10

    override fun getRefreshKey(state: PagingState<Int, HotelHistoryItem>): Int? {
        return state.anchorPosition
    }

    override suspend fun load(params: LoadParams<Int>): LoadResult<Int, HotelHistoryItem> {
        var hotelHistoryItems: List<HotelHistoryItem>? = null
        val position: Int = params.key ?: FIRST_PAGE_ITEM
        if (position == FIRST_PAGE_ITEM) isFirstPage.postValue(true)
        else isFirstPage.postValue(false)

        return when (val response =
            apiService.getHotelHistoryListResponse(token, limit, position, "all")) {
            is BaseResponse.Success -> {
                if (response.body.response?.hotelHistoryItems?.isNotEmpty() == true) {
                    hotelHistoryItems = response.body.response!!.hotelHistoryItems
                    createPage(hotelHistoryItems, position)
                } else {
                    if (position == FIRST_PAGE_ITEM) LoadResult.Error(Exception("No available data found. Please retry."))
                    else createPage(hotelHistoryItems, position)
                }
            }
            is BaseResponse.ApiError -> LoadResult.Error(Exception(response.errorBody.message))

            is BaseResponse.NetworkError -> LoadResult.Error(IOException(networkErrorMsg))

            is BaseResponse.UnknownError -> LoadResult.Error(Exception(unKnownErrorMsg))
        }
    }

    private fun createPage(
        hotelHistoryItems: List<HotelHistoryItem>?,
        position: Int,
    ) = LoadResult.Page(
        data = hotelHistoryItems.orEmpty(),
        prevKey = if (position == FIRST_PAGE_ITEM) null else position - 10,
        nextKey = if (hotelHistoryItems.isNullOrEmpty()) null else position + 10
    )

    companion object {
        const val FIRST_PAGE_ITEM = 0;
    }
}
