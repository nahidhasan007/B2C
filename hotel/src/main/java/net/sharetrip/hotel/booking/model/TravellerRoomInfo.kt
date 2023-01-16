package net.sharetrip.hotel.booking.model

import android.os.Parcelable
import com.squareup.moshi.Json
import com.squareup.moshi.Moshi
import kotlinx.android.parcel.Parcelize

@Parcelize
data class TravellerRoomInfo(
    @field:Json(name = "adults")
    var numberOfAdult: Int = 1,
    @Transient
    var numberOfChildren: Int = 0,
    @field:Json(name = "children")
    var childrenAges: MutableList<Int> = ArrayList()
) : Parcelable {
    override fun toString(): String {
        val moshi = Moshi.Builder().build()
        val jsonAdapter = moshi.adapter(TravellerRoomInfo::class.java)
        if (numberOfChildren == 0) {
            childrenAges.clear()
        } else if (numberOfChildren == 1 && childrenAges.size == 2) {
            childrenAges.removeAt(1)
        }
        return jsonAdapter.toJson(this)
    }
}
