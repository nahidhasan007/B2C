package net.sharetrip.flight.history.model

import android.os.Parcelable
import kotlinx.android.parcel.Parcelize

@Parcelize
data class Destination(
	val code: String,
	val city: String,
	val airport: String
) : Parcelable