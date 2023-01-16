package net.sharetrip.view.blog.dashboard

import androidx.databinding.ObservableBoolean
import androidx.databinding.ObservableField
import androidx.lifecycle.MutableLiveData
import com.sharetrip.base.data.PrefKey
import com.sharetrip.base.data.SharedPrefsHelper
import com.sharetrip.base.event.Event
import com.sharetrip.base.network.model.BaseResponse
import com.sharetrip.base.network.model.ErrorType
import com.sharetrip.base.network.model.RestResponse
import com.sharetrip.base.viewmodel.BaseOperationalViewModel
import net.sharetrip.model.BlogBookingItem
import net.sharetrip.model.BlogBookingResponse
import net.sharetrip.model.BlogPost
import net.sharetrip.model.BlogSliderResponse
import net.sharetrip.network.MainApiService
import java.text.NumberFormat
import java.util.*

class BlogDashboardViewModel(
    private val apiService: MainApiService,
    private val sharedPrefsHelper: SharedPrefsHelper
) : BaseOperationalViewModel() {

    private var count = 10
    private var limit = 10
    var isDataLoading = ObservableBoolean(false)
    var isLoadMoreVisible = ObservableBoolean(true)
    var blogSliderPost = MutableLiveData<List<BlogPost>>()
    var topBlogPost = MutableLiveData<List<BlogPost>>()
    var trendingTopicsPost = MutableLiveData<List<BlogPost>>()
    var topBlogList = ArrayList<BlogPost>()
    var blogBookingList = MutableLiveData<List<BlogBookingItem>>()
    val gotoFlight = MutableLiveData<Boolean>()
    val goToCategory = MutableLiveData<Event<Boolean>>()
    val goToSearch = MutableLiveData<Event<Boolean>>()
    val selectedSlug = MutableLiveData<Event<String>>()
    var formattedPoints = ObservableField<String>()

    init {
        getBlogSlider()
        getTopPost()
        getTrendingPost()
        getBlogBookingResponse()
        setPoints()
    }

    private fun setPoints() {
        var points = sharedPrefsHelper[PrefKey.USER_TRIP_COIN, ""]
        points = points.filter { it in '0'..'9' }
        if (points.isBlank()) {
            points = "0"
            sharedPrefsHelper.put(PrefKey.USER_TRIP_COIN, "0")
        }
        formattedPoints.set(NumberFormat.getNumberInstance(Locale.US).format(points.toInt()))
    }

    private fun getBlogSlider() {
        isDataLoading.set(true)
        executeSuspendedCodeBlock(
            operationTag = fetchBlogSlider,
            codeBlock = { apiService.getBlogSlider() }
        )
    }

    private fun getTopPost() {
        if (count >= limit) {
            isDataLoading.set(true)

            executeSuspendedCodeBlock(
                operationTag = fetchTopPost,
                codeBlock = { apiService.getTopBlogPost(offset = topBlogList.size, limit = limit) }
            )

        } else isLoadMoreVisible.set(false)
    }

    private fun getTrendingPost() {
        isDataLoading.set(true)
        executeSuspendedCodeBlock(
            operationTag = fetchTrendingPost,
            codeBlock = { apiService.getTrendingBlogPost() }
        )
    }

    private fun getBlogBookingResponse() {
        isDataLoading.set(true)
        executeSuspendedCodeBlock(
            operationTag = fetchBlogBooking,
            codeBlock = { apiService.getBlogBooking() }
        )
    }

    override fun onSuccessResponse(operationTag: String, data: BaseResponse.Success<Any>) {
        when (operationTag) {
            fetchBlogSlider -> {
                val response = (data.body as RestResponse<*>).response as BlogSliderResponse
                blogSliderPost.value = response.blogPostList
                isDataLoading.set(false)
            }
            fetchTopPost -> {
                val response = (data.body as RestResponse<*>).response as BlogSliderResponse
                count = response.blogPostList.size
                isDataLoading.set(false)
                topBlogPost.value = response.blogPostList
                topBlogList.addAll(response.blogPostList)
            }
            fetchTrendingPost -> {
                val response = (data.body as RestResponse<*>).response as BlogSliderResponse
                trendingTopicsPost.value = response.blogPostList
                isDataLoading.set(false)
            }
            fetchBlogBooking -> {
                val response = (data.body as RestResponse<*>).response as BlogBookingResponse
                blogBookingList.value = response.bookings
                isDataLoading.set(false)
            }
        }
    }

    override fun onAnyError(operationTag: String, errorMessage: String, type: ErrorType?) {
         showMessage(errorMessage)
    }

    fun onClickSearch() {
        goToSearch.value = Event(true)
    }

    fun onClickCategory() {
        goToCategory.value = Event(true)
    }

    fun onClickLoadMore() {
        getTopPost()
    }

    fun navigateToBlogDetails(slug: String) {
        selectedSlug.value = Event(slug)
    }


    private companion object {
        const val fetchBlogSlider = "fetch_blog_slider"
        const val fetchTopPost = "fetch_top_post"
        const val fetchTrendingPost = "fetch_trending_post"
        const val fetchBlogBooking = "fetch_blog_booking"
    }
}