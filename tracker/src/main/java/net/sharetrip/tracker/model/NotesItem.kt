package net.sharetrip.tracker.model

import com.squareup.moshi.Json

data class NotesItem(
		@field:Json(name = "country_code")
		val countryCode: String? = null,
		val date: String? = null,
		val note: String? = null,
		val sources: List<String?>? = null,
		val type: String? = null
)
