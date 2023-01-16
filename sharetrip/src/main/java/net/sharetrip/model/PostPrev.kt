package net.sharetrip.model

import android.os.Parcelable
import com.squareup.moshi.Json
import kotlinx.android.parcel.Parcelize

@Parcelize
data class PostPrev(
	@field:Json(name = "sub_title")
	val subTitle: String? = null,
	@field:Json(name = "featured_image")
	val featuredImage: String? = null,
	val slug: String? = null,
	val title: String? = null
) : Parcelable
