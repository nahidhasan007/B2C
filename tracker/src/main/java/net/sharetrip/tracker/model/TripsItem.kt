package net.sharetrip.tracker.model

import com.squareup.moshi.Json

data class TripsItem(
	@field:Json(name = "to_airport")
		val toAirport: String? = null,
	@field:Json(name = "covid19_stats")
		val covid19Stats: Covid19Stats? = null,
	@field:Json(name = "from_airport")
		val fromAirport: String? = null,
	val from: String? = null,
	val to: String? = null,
	@field:Json(name = "start_date")
		val startDate: String? = null,
	val advice: Advice? = null
)
