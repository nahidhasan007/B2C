package net.sharetrip.model

import android.os.Parcelable
import kotlinx.android.parcel.Parcelize

@Parcelize
data class TagsItem(
    val blogTag: BlogTag,
    val id: Int
) : Parcelable
