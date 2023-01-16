package net.sharetrip.holiday.booking.model

data class HolidayBookingParam(

    var adultsCount: Int = 0,
    var child7To12Count: Int = 0,
    var child3To6Count: Int = 0,
    var infantCount: Int = 0,
    var packageDate: String = "",
    var singleRoomCount: Int = 0,
    var doubleRoomCount: Int = 0,
    var twinRoomCount: Int = 0,
    var tripleRoomCount: Int = 0,
    var quadRoomCount: Int = 0,

    var cardSeries: String = "",
    var gateway: String = "",

    var arrivalTime: String = "",
    var arrivalTransportType: String = "",
    var arrivalTransportName: String = "",
    var arrivalTransportCode: String = "",
    var arrivalPickupTime: String = "",
    var arrivalPickUpLocationName: String = "",
    var arrivalAdditionalText: String = "",
    var departureTime: String = "",
    var departurePickupTime: String = "",
    var departureAdditionalText: String = "",
    var departureTransportType: String = "",
    var departureTransportName: String = "",
    var departureTransportCode: String = "",

    var packageId: Int = 0,
    var packagePeriodId: Int = 0,

    var coupon: String? = null,
    val tripCoin: Int? = null,
    var searchID: Int? = null,

    var primaryContact: PrimaryContact = PrimaryContact()
)
