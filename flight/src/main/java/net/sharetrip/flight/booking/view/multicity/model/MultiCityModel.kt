package net.sharetrip.flight.booking.view.multicity.model

import android.os.Parcelable
import kotlinx.android.parcel.Parcelize

@Parcelize
data class MultiCityModel(
        var origin: String = "",
        var originCity: String? = "",
        var originAddress: String? = "",
        var destination: String = "",
        var destinationCity: String? = "",
        var destinationAddress: String? = "",
        var departDate: String = ""
) : Parcelable