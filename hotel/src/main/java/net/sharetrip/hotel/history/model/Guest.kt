package net.sharetrip.hotel.history.model

import android.os.Parcelable
import com.squareup.moshi.Json
import kotlinx.parcelize.Parcelize

@Parcelize
data class Guest(
    @Json(name = "rooms")
    var rooms: List<List<RoomsItem>>? = null,
    @Json(name = "primaryContact")
    var primaryContact: PrimaryContact
) : Parcelable
