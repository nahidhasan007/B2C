package net.sharetrip.flight.booking.model

import com.squareup.moshi.Json

data class PriceCheckBody(

    @Json(name = "sequenceCode")
    val sequenceCode: String? = "",

    @Json(name = "searchId")
    val searchId: String? = "",

    @Json(name = "sessionId")
    val sessionId: String? = ""
)
