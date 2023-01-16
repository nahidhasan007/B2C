package net.sharetrip.flight.history.model

import com.squareup.moshi.Json

data class Travellers (
    @Json(name = "code")
    var code: String? = "",
    @Json(name = "visaCopy")
    var visaCopy: String? = "",
    @Json(name = "passportCopy")
    var passportCopy: String? = ""
)
