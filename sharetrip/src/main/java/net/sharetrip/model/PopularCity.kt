package net.sharetrip.model

import com.squareup.moshi.Json

data class PopularCity(
    val name: String,
    val state: String,
    @field:Json(name = "cityCode")
    val code: String,
    val countryCode: String,
    val countryName: String,
    @field:Json(name = "fullAddress")
    val address: String,
    val image: String
)
