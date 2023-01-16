package net.sharetrip.flight.history.model

import android.os.Parcelable
import kotlinx.android.parcel.Parcelize

@Parcelize
data class BaggageItem(
		val unit: String,
		val name: String,
		val weight: Int,
		val type: String
) : Parcelable