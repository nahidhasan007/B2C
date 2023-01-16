package net.sharetrip.flight.history.model

import android.os.Parcelable
import kotlinx.android.parcel.Parcelize

@Parcelize
data class DetailsItem(
		val price: Double,
		val name: String,
		val weight: String,
		val currency: String
) : Parcelable