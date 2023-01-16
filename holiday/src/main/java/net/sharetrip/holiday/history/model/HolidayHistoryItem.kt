package net.sharetrip.holiday.history.model

import com.squareup.moshi.Json

data class HolidayHistoryItem(
    val bookingCode: String,
    @field:Json(name = "booked_package")
    val bookedPackage: BookedPackageInfo,
    val packageDate: String,
    val currencyCode: String,
    val adultsCount: Int,
    val totalAmount: Double,
    val arrivalTime: String,
    val bookingStatus: String,
    val child3To6Count: Int,
    val child7To12Count: Int,
    val infantCount: Int,
    val paymentStatus: String,
    val tripCoin: Int
) {
    val childCount: String
        get() = (child3To6Count + child7To12Count + infantCount).toString()

    val travellers: String
        get() = "$adultsCount Adt $childCount Chd"
}
