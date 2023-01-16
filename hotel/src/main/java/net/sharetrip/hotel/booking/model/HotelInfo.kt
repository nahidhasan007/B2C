package net.sharetrip.hotel.booking.model

import android.os.Parcelable
import androidx.recyclerview.widget.DiffUtil
import com.squareup.moshi.Json
import kotlinx.android.parcel.Parcelize

@Parcelize
data class HotelInfo(
    @field:Json(name = "id")
    var id: String = "",
    @field:Json(name = "hotelId")
    var hotelId: String = "",
    @field:Json(name = "name")
    var name: String = "",
    @field:Json(name = "lowestRate")
    var lowestRate: Double = 0.0,
    @field:Json(name = "discount")
    var discount: Int = 0,
    @field:Json(name = "lowestRateAfterDiscount")
    var lowestRateAfterDiscount: Double = 0.0,
    @field:Json(name = "totalNights")
    var totalNights: String = "",
    @field:Json(name = "starRating")
    var starRating: String = "",
    @field:Json(name = "rating")
    var rating: String = "",
    @field:Json(name = "amenities")
    var amenities: MutableList<Amenity> = ArrayList(),
    @field:Json(name = "images")
    var images: MutableList<String> = ArrayList(),
    @field:Json(name = "thumbnail")
    var thumbnail: String? = null,
    @field:Json(name = "contact")
    var contactList: MutableList<Contact> = ArrayList(),
    @field:Json(name = "points")
    var points: Points? = null
) : Parcelable {

    companion object {

        var DIFF_CALLBACK: DiffUtil.ItemCallback<HotelInfo> =
            object : DiffUtil.ItemCallback<HotelInfo>() {
                override fun areItemsTheSame(oldItem: HotelInfo, newItem: HotelInfo): Boolean {
                    return oldItem.id === newItem.id
                }

                override fun areContentsTheSame(oldItem: HotelInfo, newItem: HotelInfo): Boolean {
                    return oldItem == newItem
                }
            }
    }
}
