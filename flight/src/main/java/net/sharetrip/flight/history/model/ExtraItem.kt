package net.sharetrip.flight.history.model

import android.os.Parcelable
import kotlinx.android.parcel.Parcelize

@Parcelize
data class ExtraItem(
		val route: String,
		val details: List<DetailsItem>
) : Parcelable