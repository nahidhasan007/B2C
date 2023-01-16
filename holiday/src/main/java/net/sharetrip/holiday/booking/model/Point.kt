package net.sharetrip.holiday.booking.model

import com.squareup.moshi.Json

class Point {
    @field:Json(name = "earning")
    val earningPoint: Int = 0

    @field:Json(name = "shared")
    val sharedPoint: Int = 0

    @field:Json(name = "shareLink")
    val sharedLink: String = ""
}
