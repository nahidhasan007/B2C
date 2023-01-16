package net.sharetrip.flight.history.model

import android.os.Parcelable
import com.squareup.moshi.Json
import kotlinx.android.parcel.Parcelize
import net.sharetrip.flight.history.model.ItemSegment

@Parcelize
data class Segment (
    @Json(name = "type")
    var type: String? = null,
    @Json(name = "segment")
    var segment: List<ItemSegment> = ArrayList()
): Parcelable
