package net.sharetrip.visa.history.model

import net.sharetrip.shared.utils.DateUtil
import com.squareup.moshi.Json

data class VisaHistoryDetailsResponse(
    val travellers: List<TravellersItem>? = ArrayList(),
    val entryDate: String? = "",
    val bookingCurrency: String? = "",
    val destinationCountry: String? = "",
    val totalAmount: Double? = 0.0,
    val tripCoin: Int? = 0,
    @field:Json(name = "visa_product_snapshot")
    val visaProductSnapshot: VisaProductSnapshot? = null,
    val visaStatus: String? = "",
    val bookingStatus: String? = "",
    val paymentStatus: String? = "",
    val bookingCode: String? = "",
    val travellerCount: Int? = 0,
    val exitDate: String? = "",
    val primaryContact: PrimaryContactItem? = null,
    @Json(name = "convenienceFee")
    var convenienceFee: Double = 0.0,
    @Json(name = "VATOnConvenienceFee")
    var VATOnConvenienceFee: Double = 0.0
) {
    val formattedEntryDate: String
        get() = DateUtil.parseDisplayDateFormatFromApiDateFormat(entryDate)

    val formattedExitDate: String
        get() = DateUtil.parseDisplayDateFormatFromApiDateFormat(exitDate)
}
