package net.sharetrip.model

import android.os.Parcelable
import com.squareup.moshi.Json
import kotlinx.android.parcel.Parcelize

@Parcelize
data class Category(
	val name: String? = null,
	val id: Int? = null,
	val slug: String? = null,
	@field:Json(name = "img_src")
	val imgSrc: String? = null
) : Parcelable