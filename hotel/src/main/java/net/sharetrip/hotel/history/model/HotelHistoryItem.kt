package net.sharetrip.hotel.history.model

import android.os.Parcelable
import net.sharetrip.shared.utils.DateUtil
import com.squareup.moshi.Json
import kotlinx.parcelize.Parcelize

@Parcelize
data class HotelHistoryItem(
    @Json(name = "checkin")
    var checkin: String,
    @Json(name = "totalPrice")
    var totalPrice: Int = 0,
    val afterDiscountPrice: Int,
    @Json(name = "voucherId")
    var voucherId: String = "",
    @Json(name = "specialNote")
    var specialNote: String? = "",
    @Json(name = "hotel")
    var hotel: Hotel? = null,
    @Json(name = "currency")
    var currency: String = "",
    @Json(name = "discountValue")
    var discountValue: Int = 0,
    @Json(name = "checkout")
    var checkout: String,
    @Json(name = "paymentStatus")
    var paymentStatus: String = "",
    @Json(name = "status")
    var status: String = "",
    @Json(name = "totalNights")
    var totalNights: Int = 0,
    @Json(name = "bookedRooms")
    var bookedRooms: List<BookedRoom> = ArrayList(),
    @Json(name = "tripCoin")
    var tripCoin: Int = 0,
    @Json(name = "freeCancellationDate")
    var freeCancellationDate: String?,
    @Json(name = "guests")
    var guests: Guest,
    @Json(name = "convenienceFee")
    val convenienceFee: Int = 0

) : Parcelable {
    fun getCheckingDate(): String {
        return DateUtil.getNumberOfDay(checkin)
    }

    fun getCheckoutDate(): String {
        return DateUtil.getNumberOfDay(checkout)
    }

    fun getCheckingMonthAndYear(): String {
        return DateUtil.getMonthYear(checkin)
    }

    fun getCheckoutMonthAndYear(): String {
        return DateUtil.getMonthYear(checkout)
    }
}
