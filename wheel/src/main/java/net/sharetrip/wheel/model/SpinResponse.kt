package net.sharetrip.wheel.model

import com.squareup.moshi.Json

data class SpinResponse(
    @Json(name = "code")
    var code: String,
    @Json(name = "selected")
    var selected: String,
    @Json(name = "title")
    var title: String,
    @Json(name = "message")
    var message: String,
    @field:Json(name = "tripcoinBalance")
    var tripCoinBalance: Int,
    @Json(name = "type")
    var type: Int,
    @field:Json(name = "wheels")
    var spinList: List<SpinItem>
)
