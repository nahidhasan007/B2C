package net.sharetrip.flight.booking.model.flightresponse.flights.segment

import android.os.Parcelable
import kotlinx.android.parcel.Parcelize

@Parcelize
data class HiddenStop (var city: String?, var airport: String?, var code: String?, var country: String?): Parcelable