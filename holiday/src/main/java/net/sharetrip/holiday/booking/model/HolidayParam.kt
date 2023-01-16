package net.sharetrip.holiday.booking.model

import android.os.Parcelable
import kotlinx.android.parcel.Parcelize

@Parcelize
data class HolidayParam(
    val holidayId: Int,
    val offerId: Int,
    var packageDate: String = "",
    var currency: String,
    var gatewayCurrency: String,
    var cardSeries: String = "",
    var gateway: String = "",

    var singleRoom: Int = 0,
    var doubleRoom: Int = 0,
    var twinRoom: Int = 0,
    var tripleRoom: Int = 0,
    var quadRoom: Int = 0,

    var singleRoomPrice: Double = 0.0,
    var doubleRoomPrice: Double = 0.0,
    var twinRoomPrice: Double = 0.0,
    var tripleRoomPrice: Double = 0.0,
    var quadRoomPrice: Double = 0.0,

    var adult: Int = 0,
    var infant: Int = 0,
    var child3to6: Int = 0,
    var child7to12: Int = 0,

    var infantPrice: Double = 0.0,
    var child3to6Price: Double = 0.0,
    var child7to12Price: Double = 0.0,

    var withAirFare: Boolean = false,
    var arrivalType: String = "",
    var arrivalTransportName: String = "",
    var arrivalTransportCode: String = "",
    var arrivalPickupTime: String = "",

    var departureType: String = "",
    var departureTransportName: String = "",
    var departureTransportCode: String = "",
    var departureTime: String = "",
    var pickupTime: String = "",
    var searchID: Int = 0,

    var totalAmount: Double = 0.0,
    var discountAmount: Double = 0.0,
    val earnPoint: Int,
    val sharedPoint: Int,
    var hotelNames: String = "",
    var hotelCity: String = "",
    var cancelPolicy: String = "",
    var hasCancelPolicy: Boolean = false,
    var cancelPolicyDate: String = "",
    var releaseTime: Int = 0,
    var bankGatewayList: List<String> = ArrayList()
) : Parcelable {

    fun isHolidayReservationValid() =
        (singleRoom + doubleRoom + twinRoom + tripleRoom + quadRoom > 0 && adult > 0 && packageDate.isNotEmpty())

    fun isBookingValid() = (arrivalType.isNotEmpty() && arrivalTransportName.isNotEmpty()
            && arrivalTransportCode.isNotEmpty() && arrivalPickupTime.isNotEmpty() && departureType.isNotEmpty()
            && departureTransportName.isNotEmpty() && departureTransportCode.isNotEmpty()
            && departureTime.isNotEmpty() && pickupTime.isNotEmpty())

}