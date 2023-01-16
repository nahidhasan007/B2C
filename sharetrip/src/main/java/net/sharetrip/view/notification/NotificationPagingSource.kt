package net.sharetrip.view.notification

import androidx.paging.PagingSource
import androidx.paging.PagingState
import net.sharetrip.shared.model.UserNotification
import net.sharetrip.model.RestDealsResponse
import net.sharetrip.network.MainApiService
import retrofit2.HttpException
import java.io.IOException

class NotificationPagingSource(private val apiService: MainApiService) : PagingSource<Int, UserNotification>() {

    override fun getRefreshKey(state: PagingState<Int, UserNotification>): Int? {
        return state.anchorPosition
    }

    override suspend fun load(params: LoadParams<Int>): LoadResult<Int, UserNotification> {
        val position = params.key ?: STARTING_INDEX
        return try {
            val response = apiService.fetchDealsList(position, PAGE_SIZE)
            val notifications = convertDealToNotification(response)
            LoadResult.Page(
                data = notifications,
                prevKey = if (position == STARTING_INDEX) null else position - 10,
                nextKey = if (notifications.isNullOrEmpty()) null else position + 10
            )
        } catch (e: IOException) {
            e.printStackTrace()
            LoadResult.Error(e)
        } catch (e: HttpException) {
            e.printStackTrace()
            LoadResult.Error(e)
        }
    }

    private fun convertDealToNotification(dealsResponse: RestDealsResponse?): List<UserNotification> {
        val deals = ArrayList<UserNotification>()

        dealsResponse?.response?.data?.filter { deal ->
            deal.platform.contains("android")
        }?.forEach { deal ->
            val notification = UserNotification(
                title = deal.title,
                description = deal.description,
                imageUrl = deal.imageUrl,
                platform = "android",
                timeStamp = deal.timeStamp,
                tigerBy = deal.triggeredBy,
                comment = "N/A",
                publishDate = deal.publishDate
            )
            deals.add(notification)
        }
        return deals
    }

    companion object {
        const val STARTING_INDEX = 0
        const val PAGE_SIZE = 10
    }
}
