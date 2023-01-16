package net.sharetrip.visa.booking.model.payment

import com.squareup.moshi.Json

data class RemoteDiscountModel(
    @Json(name = "subtitle")
    val subtitle: String,
    @Json(name = "title")
    val title: String,
    @Json(name = "type")
    val type: String
)
