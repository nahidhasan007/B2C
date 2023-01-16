package net.sharetrip.tracker.model

import com.squareup.moshi.Json
import java.util.*

data class TravelAdviceResponse(
	@field:Json(name = "risk_level")
		var riskLevel: String,
	@field:Json(name = "start_date")
		val startDate: String? = null,
	val requirements: Requirements? = null,
	val userID: String? = null,
	val trips: List<TripsItem> = ArrayList(),
	val name: String? = null,
	val recommendation: String? = null
)
