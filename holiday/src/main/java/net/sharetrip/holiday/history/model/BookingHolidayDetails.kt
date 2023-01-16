package net.sharetrip.holiday.history.model

import net.sharetrip.shared.utils.DateUtil
import com.squareup.moshi.Json
import net.sharetrip.holiday.booking.model.PrimaryContact

data class BookingHolidayDetails(
    val bookingCode: String,
    val packageDate: String,
    val totalAmount: Int,
    val promotionalAmount: Int,
    val convenienceFee: Int,
    val bookingCurrency: String,
    val paymentStatus: String,
    val bookingStatus: String,
    val adultsCount: Int,
    val child3To6Count: Int,
    val child7To12Count: Int,
    val infantCount: Int,
    val departurePickupTime: String,
    val tripCoin: Int,
    val couponValue: Int,
    val hotels: String,
    @field:Json(name = "booked_package")
    val bookedPackage: BookedPackageInfo,
    val primaryContact: PrimaryContact
) {
    val childCount: String
        get() = (child3To6Count + child7To12Count + infantCount).toString()

    val travellers: String
        get() = "$adultsCount Adt $childCount Chd"

    val dateFigure: String
        get() {
            var value = ""
            try {
                value = DateUtil.getNumberOfDay(packageDate)
            } catch (e: Exception) {
                e.printStackTrace()
            }
            return value
        }

    val monthAndYear: String
        get() {
            var value = ""
            try {
                value = DateUtil.getMonthYearTrimmed(packageDate)
            } catch (e: Exception) {
                e.printStackTrace()
            }
            return value
        }
}
