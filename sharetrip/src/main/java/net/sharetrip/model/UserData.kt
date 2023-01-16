package net.sharetrip.model

import android.os.Parcelable
import com.squareup.moshi.Json
import kotlinx.android.parcel.Parcelize

@Parcelize
data class UserData(
	@field:Json(name = "country_code")
	val countryCode: String,
	val continent: String,
	val country: String
) : Parcelable