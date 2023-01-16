package net.sharetrip.hotel.booking.model

import android.os.Parcelable
import android.text.TextUtils
import com.squareup.moshi.Json
import kotlinx.parcelize.Parcelize

@Parcelize
data class GuestInfo(
    @field:Json(name = "titleName")
    var titleName: String? = null,
    @field:Json(name = "surName")
    var surName: String? = null,
    @field:Json(name = "givenName")
    var givenName: String? = null,
    @field:Json(name = "type")
    var type: String? = null,
    @field:Json(name = "nationality")
    var nationality: String? = null
) : Parcelable {

    val adultType: GuestInfo
        get() {
            type = RoomGuestType.ARG_ADULT
            return this
        }

    val childType: GuestInfo
        get() {
            type = RoomGuestType.ARG_CHILD
            return this
        }

    val isInputDataValid: Boolean
        get() = (!TextUtils.isEmpty(surName)
                && !TextUtils.isEmpty(givenName)
                && !TextUtils.isEmpty(nationality))
}
