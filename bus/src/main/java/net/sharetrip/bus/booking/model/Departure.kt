package net.sharetrip.bus.booking.model

import android.os.Parcelable
import kotlinx.parcelize.Parcelize
import java.text.SimpleDateFormat

@Parcelize
data class Departure(
    var arrivalTime: String?,
    val availableSeats: String,
    var boardingPoints: List<BoardingPoints>?,
    var coachNo: String,
    val coachType: String,
    var company: Company,
    var date: String?,
    var departureTime: String?,
    var droppingPoints: List<BoardingPoints>?,
    var discount: String?,
    var endCounter: String,
    val fare: String,
    val id: String,
    var discountedFare: String,
    val obsolete: Boolean,
    var route: Route,
    val seatCol: Int,
    val seatRow: Int,
    val seats: List<Seat>?,
    var serviceCharge: String = "0",
    var startCounter: String,
    var duration: Duration?,
    var departureMilliSeconds: Long?,
    var selectedSeatId: MutableList<String>,
    var selectedSeatsNo: MutableList<String>?,
    var selectedSeatsInString: String?,
    var totalPayable: String?,
    var selectedSeatsInfo: MutableList<Seat>?,
    var cartId: String?,
    var isValidDuration: Boolean = false
) : Parcelable {

    fun setTimeDuration() {
        duration = if (departureTime != null && arrivalTime != null) {
            getTimeDifference(
                departureTime!!,
                arrivalTime!!
            )
        } else {
            Duration()
        }
        isValidDuration = !(duration!!.hour == 0L && duration!!.minute == 0L)
    }

    fun getTimeDifference(startTime: String, endTime: String): Duration {
        return try {
            val format = SimpleDateFormat("hh:mm a")
            val date1 = format.parse(startTime)
            val date2 = format.parse(endTime)

            val diff: Long = date2.time - date1.time
            var diffMinutes: Long = diff / (60 * 1000) % 60
            var diffHours: Long = diff / (60 * 60 * 1000) % 24
            if (diffHours <= 0) {
                diffHours += 24
            }
            if (diffMinutes < 0) {
                diffMinutes += 60
                if (diffHours > 0) {
                    diffHours -= 1
                }
            }
            return Duration(diffHours, diffMinutes)
        } catch (exception: Exception) {
            Duration()
        }
    }

    // TODO remove on api stability // replace this with actual "discount" where "nonNullDiscount" is used
    val nonNullDiscount: String
        get() = if (discount.isNullOrEmpty()) "0" else discount!!
}
