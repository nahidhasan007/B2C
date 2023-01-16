package net.sharetrip.tour.model

import android.os.Parcelable
import kotlinx.parcelize.Parcelize

@Parcelize
data class TourPopCity(
    val cityCode: String = "",
    val countryCode: String? = null,
    val countryName: String? = null,
    val image: String? = null,
    val name: String? = null
) : Parcelable
