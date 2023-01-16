package net.sharetrip.model

import android.os.Parcelable
import android.widget.ImageView
import androidx.databinding.BindingAdapter
import com.sharetrip.base.utils.ShareTripAppConstants
import net.sharetrip.shared.utils.loadImageWithCenterCrop
import com.squareup.moshi.Json
import kotlinx.android.parcel.Parcelize
import java.util.*
import java.util.regex.Pattern
import kotlin.collections.ArrayList

@Parcelize
data class BlogPost(
    @field:Json(name = "author_name")
    val authorName: String?,
    @field:Json(name = "meta_description")
    val metaDescription: String?,
    @field:Json(name = "sub_title")
    val subTitle: String?,
    @field:Json(name = "meta_title")
    val metaTitle: String?,
    @field:Json(name = "meta_keywords")
    val metaKeywords: String?,
    @field:Json(name = "featured_image")
    val featuredImage: String?,
    val title: String,
    val slug: String,
    val content: String?,
    val category: Category,
    val tags: List<TagsItem>? = ArrayList()
) : Parcelable {

    fun formatContentData(): String {
        val justifyTag = "<html><body style='text-align:justify;'>%s</body></html>"
        content?.let {

            var data = it.replace("&lt;", "<").replace("&gt;", ">")

            val tagList = arrayOf("flightnode", "hotelnode ", "packagenode")

            for (tag in tagList) {
                val patternString = "(?is)<$tag[^>]+>.*?<\\/$tag>"
                val pattern = Pattern.compile(patternString)
                val matcher = pattern.matcher(data)
                println("\n\n" + matcher.find())
                data = matcher.replaceAll("")
            }

            return java.lang.String.format(Locale.US, justifyTag, data)
        }
        return ""
    }

    fun shareContentURL(): String {
        return ShareTripAppConstants.BLOG_SHARE_URL + slug
    }
}

@BindingAdapter("image")
fun setImageViewResource(imageView: ImageView, url: String?) {
        imageView.loadImageWithCenterCrop(url)
}
