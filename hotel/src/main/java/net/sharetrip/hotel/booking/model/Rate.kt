package net.sharetrip.hotel.booking.model

import android.os.Parcelable
import com.squareup.moshi.Json
import kotlinx.android.parcel.Parcelize

@Parcelize
data class Rate(
    @field:Json(name = "id")
    var id: String = "",
    @field:Json(name = "name")
    var name: String = "",
    @field:Json(name = "description")
    var description: String = "",
    @field:Json(name = "price")
    var price: Price = Price(),
    @field:Json(name = "points")
    var points: Point = Point(),
    @field:Json(name = "totalNights")
    var totalNights: Int = 0,
    @field:Json(name = "size")
    var size: String = "",
    @field:Json(name = "features")
    var features: MutableList<String> = ArrayList(),
    @field:Json(name = "cancellationPolicy")
    var cancellationPolicy: CancellationPolicy = CancellationPolicy(),
    @field:Json(name = "meals")
    var meal: MutableList<String> = ArrayList(),
    @field:Json(name = "roomQuantities")
    var roomQuantities: MutableList<RoomQuantities> = ArrayList(),
    @field:Json(name = "roomsText")
    var roomsText: MutableList<String> = ArrayList(),
    @field:Json(name = "smokingPolicies")
    var smokingPolicies: Boolean?,
    @field:Json(name = "amenities")
    var amenities: MutableList<String> = ArrayList(),
    @field:Json(name = "serpFilters")
    var serpFilters: MutableList<String> = ArrayList(),
    var gatewayCurrency: String,
    var providerCode: String = "",
    var roomIds: MutableList<Int> = ArrayList()
) : Parcelable
