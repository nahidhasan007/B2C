package net.sharetrip.hotel.history.model

import com.squareup.moshi.Json

data class BookingVoucher(
    @Json(name = "voucherId")
    var voucherId: String? = null
)
