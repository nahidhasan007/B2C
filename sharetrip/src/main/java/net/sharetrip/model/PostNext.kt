package net.sharetrip.model

import android.os.Parcelable
import com.squareup.moshi.Json
import kotlinx.android.parcel.Parcelize

@Parcelize
data class PostNext(
	@Json(name = "sub_title")
	val subTitle: String? = null,
	@Json(name = "featured_image")
	val featuredImage: String?,
	val title: String? = null,
	val slug: String? = null
) : Parcelable