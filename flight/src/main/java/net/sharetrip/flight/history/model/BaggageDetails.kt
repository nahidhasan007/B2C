package net.sharetrip.flight.history.model

import android.os.Parcelable
import kotlinx.android.parcel.Parcelize

@Parcelize
data class BaggageDetails(
	val extra: List<ExtraItem>?,
	val basic: List<BasicItem>
) : Parcelable