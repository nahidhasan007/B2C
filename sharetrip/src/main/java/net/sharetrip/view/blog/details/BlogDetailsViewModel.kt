package net.sharetrip.view.blog.details

import android.content.Intent
import androidx.databinding.ObservableBoolean
import androidx.databinding.ObservableField
import androidx.lifecycle.MutableLiveData
import com.sharetrip.base.data.PrefKey
import com.sharetrip.base.data.SharedPrefsHelper
import com.sharetrip.base.event.Event
import com.sharetrip.base.network.model.BaseResponse
import com.sharetrip.base.network.model.RestResponse
import com.sharetrip.base.viewmodel.BaseOperationalViewModel
import net.sharetrip.model.BlogDetailsResponse
import net.sharetrip.model.BlogPost
import net.sharetrip.model.TagsItem
import net.sharetrip.network.MainApiService
import java.text.NumberFormat
import java.util.*

class BlogDetailsViewModel(
    val apiService: MainApiService,
    val slug: String?,
    val sharedPrefsHelper: SharedPrefsHelper
) :
    BaseOperationalViewModel() {

    var isDataLoading = ObservableBoolean(false)
    var blogPost = ObservableField<BlogPost>()
    var youMayLikeBlogList = MutableLiveData<List<BlogPost>>()
    var blogTagList = MutableLiveData<List<TagsItem>>()
    val shareIntent = MutableLiveData<Event<Intent>>()
    var formattedPoints = ObservableField<String>()

    init {
        setPoints()
        getBlogDetails()
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

    private fun getBlogDetails() {
        isDataLoading.set(true)
        slug?.let {
            executeSuspendedCodeBlock(
                operationTag = getBlogDetails,
                codeBlock = { apiService.getBlogDetails(it) }
            )
        }
    }

    override fun onSuccessResponse(operationTag: String, data: BaseResponse.Success<Any>) {
        when (operationTag) {
            getBlogDetails -> {
                isDataLoading.set(false)
                val response = (data.body as RestResponse<*>).response as BlogDetailsResponse
                blogPost.set(response.blogPost)
                youMayLikeBlogList.value = response.postSuggestions as List<BlogPost>
                blogTagList.value = response.blogPost?.tags!!
            }
        }
    }

    fun onClickShareBlog() {
        val sendIntent = Intent().apply {
            action = Intent.ACTION_SEND
            putExtra(Intent.EXTRA_TEXT, blogPost.get()?.shareContentURL())
            type = "text/plain"
        }

        shareIntent.value = Event(sendIntent)
    }

    private companion object {
        const val getBlogDetails = "get_blog_details"
    }

}