package net.sharetrip.bus.booking.model

import android.os.Parcelable
import kotlinx.parcelize.Parcelize

@Parcelize
data class SearchIdAndTripCoin(
    var redeemableTripCoin: String,
    var searchId: String,
) : Parcelable
