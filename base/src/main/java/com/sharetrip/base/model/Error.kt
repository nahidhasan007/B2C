package com.sharetrip.base.model

import com.sharetrip.base.network.model.TraceValue
import com.squareup.moshi.Json

data class Error(
    val message: String?,

    @Json(name = "trace")
    val trace: List<TraceValue>?
)
