package net.sharetrip.tour.model

data class TourBookingDetails(
    val VATOnConvenienceFee: Int,
    val adultsCount: Int,
    val booked_transfer: BookedTransfer,
    val bookingCode: String,
    val bookingCurrency: String,
    val bookingStatus: String,
    val child3To6Count: Int,
    val child7To12Count: Int,
    val convenienceFee: Int,
    val couponValue: Int,
    val departureTime: String,
    val infantCount: Int,
    val paymentStatus: String,
    val primaryContact: PrimaryContact,
    val totalAmount: Int,
    val tourDate: String,
    val tripCoin: Int
)
