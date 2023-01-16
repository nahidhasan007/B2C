package net.sharetrip.flight.booking.model

import com.squareup.moshi.Json
import java.util.*

data class Passengers (
        @Json(name = "adult")
        var adult: List<ItemTraveler> = ArrayList(),
        @Json(name = "child")
        var child: List<ItemTraveler> = ArrayList(),
        @Json(name = "infant")
        var infant: List<ItemTraveler> = ArrayList()
)
