package net.sharetrip.model

import android.os.Parcelable
import kotlinx.android.parcel.Parcelize

@Parcelize
data class BlogTag(
	val id: Int,
	val title: String,
	val slug: String
) : Parcelable