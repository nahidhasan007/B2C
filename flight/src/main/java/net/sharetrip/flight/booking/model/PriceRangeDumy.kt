package net.sharetrip.flight.booking.model

import android.os.Parcelable
import com.squareup.moshi.Moshi
import kotlinx.android.parcel.Parcelize

@Parcelize
data class PriceRangeDumy (
        var low: Int = 0,
        var high: Int = 0,
        var lowProgress: Int = 0,
        var highProgress: Int = 0
) : Parcelable
