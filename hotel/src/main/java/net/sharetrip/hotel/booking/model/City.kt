package net.sharetrip.hotel.booking.model

data class City(
    val id: Int,
    val cityCode: String,
    val name: String? = null,
    val country_code: String,
    val country_name: String,
    val state: String? = null
)
