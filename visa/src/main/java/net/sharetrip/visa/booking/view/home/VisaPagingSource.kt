package net.sharetrip.visa.booking.view.home

import androidx.lifecycle.MutableLiveData
import androidx.paging.PagingSource
import androidx.paging.PagingState
import com.sharetrip.base.network.model.BaseResponse
import net.sharetrip.visa.booking.model.VisaItem
import net.sharetrip.visa.network.VisaBookingApiService
import net.sharetrip.visa.utils.MsgUtils.networkErrorMsg
import net.sharetrip.visa.utils.MsgUtils.unKnownErrorMsg
import java.io.IOException

class VisaPagingSource(private val apiService: VisaBookingApiService, val nationality: String) :
    PagingSource<Int, VisaItem>() {
    private val limit = 10
    val isFirstPage = MutableLiveData(false)

    override fun getRefreshKey(state: PagingState<Int, VisaItem>): Int? {
        return state.anchorPosition
    }

    override suspend fun load(params: LoadParams<Int>): LoadResult<Int, VisaItem> {
        var visaItems: List<VisaItem>? = null
        val position: Int = params.key ?: FIRST_PAGE_ITEM
        if (position == FIRST_PAGE_ITEM) isFirstPage.postValue(true)
        else isFirstPage.postValue(false)

        return when (val response =
            apiService.fetchVisaCountryList(position, limit, nationality)) {
            is BaseResponse.Success -> {
                if (response.body.response?.data?.isNotEmpty() == true) {
                    visaItems = response.body.response.data
                    createPage(visaItems, position)
                } else {
                    if (position == FIRST_PAGE_ITEM) LoadResult.Error(Exception("No available data found. Please retry."))
                    else createPage(visaItems, position)
                }
            }
            is BaseResponse.ApiError -> LoadResult.Error(Exception(response.errorBody.message))

            is BaseResponse.NetworkError -> LoadResult.Error(IOException(networkErrorMsg))

            is BaseResponse.UnknownError -> LoadResult.Error(Exception(unKnownErrorMsg))
        }
    }

    private fun createPage(
        visaItems: List<VisaItem>?,
        position: Int
    ) = LoadResult.Page(
        data = visaItems.orEmpty(),
        prevKey = if (position == FIRST_PAGE_ITEM) null else position - 10,
        nextKey = if (visaItems.isNullOrEmpty()) null else position + 10
    )

    companion object {
        const val FIRST_PAGE_ITEM = 0;
    }
}
