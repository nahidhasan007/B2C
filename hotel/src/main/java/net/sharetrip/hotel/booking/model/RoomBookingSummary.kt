package net.sharetrip.hotel.booking.model

import java.text.NumberFormat
import java.util.*
import kotlin.math.ceil

class RoomBookingSummary(var conversionRate: Double = 1.0) {
    var totalNight = 0
    var roomName = ""
    var totalRoom = 0
    var dateList: MutableList<String> = ArrayList()
    var propertyName: String? = ""
    var searchId: String? = ""
    var hotelId: String? = ""
    var roomSearchCode: String? = ""
    var propertyRoomId: String? = ""

    var earnTripCoin = 0
        get() {
            var count = 0
            if (firstRoomAdult > 0) {
                count++
            }
            if (secondRoomAdult > 0) {
                count++
            }
            if (thirdRoomAdult > 0) {
                count++
            }
            if (fourthRoomAdult > 0) {
                count++
            }
            return field * count
        }
    var firstRoomAdult = 0
    var firstRoomChild = 0
    var firstRoomCost = 0.0
    var firstRoomDiscountedCost = 0.0
    var secondRoomAdult = 0
    var secondRoomChild = 0
    var secondRoomCost = 0
    var secondRoomDiscountedCost = 0
    var thirdRoomAdult = 0
    var thirdRoomChild = 0
    var thirdRoomCost = 0
    var thirdRoomDiscountedCost = 0
    var fourthRoomAdult = 0
    var fourthRoomChild = 0
    var fourthRoomCost = 0
    var fourthRoomDiscountedCost = 0
    var gatewayType = ""
    var providerCode = ""
    var propertyCode = ""
    var rooms: MutableList<Int> = ArrayList()

    fun firstRoomPayable(): String {
        return NumberFormat.getNumberInstance(Locale.US)
            .format(ceil((totalNight * firstRoomCost.toLong()) / conversionRate).toInt())
    }

    fun firstRoomPayableAvg(): String {
        return NumberFormat.getNumberInstance(Locale.US)
            .format(ceil((firstRoomCost / totalRoom) / conversionRate))
    }

    fun firstRoomPayablePerNight(): String {
        return NumberFormat.getNumberInstance(Locale.US)
            .format(firstRoomCost.toLong() / conversionRate)
    }

    fun secondRoomPayable(): String {
        return NumberFormat.getNumberInstance(Locale.US)
            .format((secondRoomCost * totalNight.toLong()) / conversionRate)
    }

    fun thirdRoomPayable(): String {
        return NumberFormat.getNumberInstance(Locale.US)
            .format((thirdRoomCost * totalNight.toLong()) / conversionRate)
    }

    fun fourthRoomPayable(): String {
        return NumberFormat.getNumberInstance(Locale.US)
            .format((fourthRoomCost * totalNight.toLong()) / conversionRate)
    }

    fun totalPayableAmountWithComma(): String {
        val total =
            firstRoomCost * totalNight + secondRoomCost * totalNight + thirdRoomCost * totalNight + fourthRoomCost * totalNight
        return NumberFormat.getNumberInstance(Locale.US).format(total.toLong())
    }

    fun totalPayableAmount(): Double {
        return totalPayableBeforeDiscount() - totalDiscount()
    }

    fun totalPayableBeforeDiscount(): Double {
        return (firstRoomCost * totalNight + secondRoomCost * totalNight + thirdRoomCost * totalNight + fourthRoomCost * totalNight) / conversionRate
    }

    fun totalPayableAfterDiscount(): Double {
        return (firstRoomCost * totalNight + secondRoomCost * totalNight + thirdRoomCost * totalNight + fourthRoomCost * totalNight) - totalDiscount() / conversionRate
    }

    fun totalPayableBeforeDiscountInt(): Int {
        return ceil((firstRoomCost * totalNight + secondRoomCost * totalNight + thirdRoomCost * totalNight + fourthRoomCost * totalNight) / conversionRate).toInt()
    }

    fun totalDiscount(): Double {
        val total =
            firstRoomCost * totalNight + secondRoomCost * totalNight + thirdRoomCost * totalNight + fourthRoomCost * totalNight
        val discountedTotal =
            firstRoomDiscountedCost * totalNight + secondRoomDiscountedCost * totalNight + thirdRoomDiscountedCost * totalNight + fourthRoomDiscountedCost * totalNight
        return (total - discountedTotal) / conversionRate
    }
}
