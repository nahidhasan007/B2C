package net.sharetrip.visa.history.model

import android.os.Parcelable
import kotlinx.android.parcel.Parcelize

@Parcelize
data class VisaProductSnapshot(
    val visaFee: Double? = null,
    val discount: Int? = 0,
    val bookingCurrency: String? = null,
    val visaPrepMinDays: Int? = null,
    val title: String? = null,
    val type: String? = null,
    val processingFee: Double? = null,
    val productCode: String? = null,
    val specialNote: String? = null,
    val courierCharge: Int? = null,
    val discountType: String? = null,
    val cancelCharge: Int? = null,
    val cancellationPolicy: String? = "",
    val validityDays: Int? = null,
    val maxStayDays: Int? = null
) : Parcelable