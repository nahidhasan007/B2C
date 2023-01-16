package net.sharetrip.holiday.booking.model

import com.squareup.moshi.Json

class HolidayBookingResponse {
    @Json(name = "url")
    var url: String? = null
}
