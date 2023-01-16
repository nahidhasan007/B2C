package net.sharetrip.view.blog.search

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
import net.sharetrip.model.BlogPost
import net.sharetrip.model.BlogSliderResponse
import net.sharetrip.network.MainApiService
import java.text.NumberFormat
import java.util.*

class SearchBlogViewModel(private val apiService: MainApiService, slug: String?, val sharedPrefsHelper: SharedPrefsHelper) :
    BaseOperationalViewModel() {
    var searchBlogPost = MutableLiveData<List<BlogPost>>()
    var isDataLoading = ObservableBoolean(false)
    var searchText = ObservableField<String>()
    var searchResult = ObservableField<String>()
    var goToCategory = MutableLiveData<Event<Boolean>>()
    var formattedPoints = ObservableField<String>()

    init {
        setPoints()
        if (!slug.isNullOrEmpty()) {
            searchText.set(slug)
            getSearchResult(slug)
        }
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

    private fun getSearchResult(searchText: String) {
        isDataLoading.set(true)
        executeSuspendedCodeBlock(
            operationTag = getBlogSearchResult,
            codeBlock = {
                apiService.getBlogSearchResults(
                    text = searchText,
                    offset = 0,
                    limit = 10
                )
            }
        )
    }

    override fun onSuccessResponse(operationTag: String, data: BaseResponse.Success<Any>) {
        when (operationTag) {
            getBlogSearchResult -> {
                isDataLoading.set(false)
                val response = (data.body as RestResponse<*>).response as BlogSliderResponse
                searchBlogPost.value = response.blogPostList
                searchResult.set("" + response.blogPostList.size + " articles found for \"${searchText.get()}\"")
            }
        }
    }

    override fun onAnyError(operationTag: String, errorMessage: String, type: ErrorType?) {
        isDataLoading.set(false)
    }

    fun onClickCategory() {
        goToCategory.value = Event(true)
    }

    fun onSearchClicked() {
        if (!searchText.get().isNullOrEmpty())
            getSearchResult(searchText.get().toString())
        else
             showMessage("Please enter a valid keyword.")
    }

    private companion object {
        const val getBlogSearchResult = "get_blog_search_result"
    }
}