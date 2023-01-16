package net.sharetrip.visa.history.model

import android.os.Parcelable
import net.sharetrip.shared.utils.DateUtil
import com.squareup.moshi.Json
import kotlinx.android.parcel.Parcelize

@Parcelize
data class VisaHistoryItem(
    val totalAmount: Double,
    val visaStatus: String,
    val tripCoin: Int,
    val bookingCurrency: String,
    val bookingStatus: String,
    val bookingCode: String,
    val entryDate: String,
    val exitDate: String,
    val travellerCount: Int,
    val destinationCountry: String,
    val paymentStatus: String,
    @field:Json(name = "visa_product_snapshot")
    val visaProductSnapshot: VisaHistoryProductSnapshot,
    var showingStatus: String
) : Parcelable {
    val formattedEntryDate: String
        get() = DateUtil.parseDisplayDateFormatFromApiDateFormat(entryDate)
}
