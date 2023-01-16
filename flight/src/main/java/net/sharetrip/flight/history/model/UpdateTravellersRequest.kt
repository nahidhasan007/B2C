package net.sharetrip.flight.history.model

import com.squareup.moshi.Json
import net.sharetrip.flight.history.model.Travellers

data class UpdateTravellersRequest (
    @Json(name = "bookingCode")
    var bookingCode: String? = null,
    @Json(name = "travellers")
    var travellers: List<Travellers> = ArrayList()
)
