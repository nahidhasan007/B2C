package net.sharetrip.bus.history.model

import android.os.Parcelable
import kotlinx.parcelize.Parcelize

@Parcelize
data class HistoryDetails(
    val boardingPoint: String,
    val boardingTime: String,
    val bookingId: String,
    val bookingStatus: Int,
    val busOperator: String,
    val cancelledAt: String?,
    val cardSeries: String,
    val coachNumber: String,
    val coachType: String,
    val confirmedAt: String,
    val conversionRate: Int,
    val couponAmount: Int,
    val couponCode: String?,
    val couponType: Int,
    val currency: String,
    val discount: Double,
    val discountType: Int,
    val droppingPoint: String,
    val droppingTime: String,
    val gateway: String,
    val gatewayAmount: Double,
    val gatewayCurrency: String,
    val journeyDate: String,
    val markup: Int,
    val markupType: Int,
    val passengerEmail: String,
    val passengerFirstName: String,
    val passengerGender: String,
    val passengerLastName: String,
    val passengerPhone: String,
    val paymentStatus: Int,
    val route: String,
    val seats: List<SeatInHistory>,
    val serviceCharge: Int,
    val ticketExpireTime: String,
    val ticketNumber: String,
    val tripCoin: Int,
    val tripCoinType: Int,
    var seatsNo: String = ""
) : Parcelable
