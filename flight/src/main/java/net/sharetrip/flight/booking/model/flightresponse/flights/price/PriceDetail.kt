package net.sharetrip.flight.booking.model.flightresponse.flights.price

import android.os.Parcelable
import kotlinx.android.parcel.Parcelize

@Parcelize
data class PriceDetail(
    var type: String,
    var baseFare: String,
    var tax: String,
    var total: String,
    var currency: String,
    var numberPaxes: String
) : Parcelable
