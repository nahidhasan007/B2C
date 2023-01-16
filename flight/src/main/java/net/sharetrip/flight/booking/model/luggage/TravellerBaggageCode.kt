package net.sharetrip.flight.booking.model.luggage

import android.os.Parcelable
import kotlinx.android.parcel.Parcelize

@Parcelize
data class TravellerBaggageCode(
        var luggageCodeList: ArrayList<String> = ArrayList(),
        var totalBaggageCost: Double = 0.0) : Parcelable