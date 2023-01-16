package net.sharetrip.model

import android.os.Parcelable
import com.squareup.moshi.Json
import kotlinx.android.parcel.Parcelize

@Parcelize
data class BlogDetailsResponse(
	@field:Json(name = "post_next")
	val postNext: PostNext? = null,
	@field:Json(name = "post_prev")
	val postPrev: PostPrev? = null,
	@field:Json(name = "post_suggestions")
	val postSuggestions: List<BlogPost?>? = ArrayList(),
	@field:Json(name = "blog_post")
	val blogPost: BlogPost?,
	@field:Json(name = "user_data")
	val userData: UserData?
) : Parcelable
