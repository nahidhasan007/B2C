package com.sharetrip.base.network.model

import com.sharetrip.base.model.Error
import com.squareup.moshi.Json

data class RestResponse<T> (
        @Json(name = "code")
        var code: String,

        @Json(name = "message")
        var message: String,

        @Json(name = "response")
        var response: T,

        @Json(name = "errors")
        var errors: Error?
)
