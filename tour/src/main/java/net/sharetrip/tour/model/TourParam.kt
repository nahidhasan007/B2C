package net.sharetrip.tour.model

import android.os.Parcelable
import kotlinx.android.parcel.Parcelize

@Parcelize
data class TourParam(

    var tourId: Int = 0,
    var tourOfferId: Int = 0,
    var tourDate: String = "",
    var adult: Int = 0,
    var child7to12: Int = 0,
    var child3to6: Int = 0,
    var infant: Int = 0,
    var departureTime: String = "",
    var pickUpLocation: String = "",
    var countryCode: String = "",
    var additionalReq: String = "",
    var bookingCurrency: String = "",
    var tourDuration: String = "",
    var cityName: String = "",
    var countryName: String = "",
    var offerNo: Int = 0,
    var earnCoin: Int = 0,
    var totalAmount: Int = 0,
    var adultAmount: Int = 0,
    var infantAmount: Int = 0,
    var child7t12Amount: Int = 0,
    var child3t6Amount: Int = 0,
    var releaseTime: Int = 0,
    var cxlPolicy: String = ""
) : Parcelable {

    fun inDataValid(): Boolean {
        return tourId != 0 && tourOfferId != 0 && adult > 0
    }
}
