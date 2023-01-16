package net.sharetrip.flight.history.model

import com.squareup.moshi.Json

class RetryPayment {
    @Json(name = "code")
    var code: String? = null
    @Json(name = "message")
    var message: String? = null
    @Json(name = "response")
    var response: Any? = null
}
