package net.sharetrip.flight.history.model

import android.os.Parcelable
import kotlinx.parcelize.Parcelize
import kotlin.math.ceil

@Parcelize
data class RefundQuotationResponse(
    val airlineRefundCharge: Int,
    val confirmationTime: Int,
    val purchasePrice: Int,
    val refundSearchId: String,
    val specialRefundMessage: String,
    val stFee: Int,
    val totalFee: Int,
    val totalRefundAmount: Int,
    var currency: String? = "BDT",
    var VATOnConvenienceFee: Double,
    var total: Double
) : Parcelable {
    fun calculatePricing() {
        VATOnConvenienceFee = 0.0
        total =
            ceil(totalRefundAmount - totalFee - stFee - VATOnConvenienceFee - airlineRefundCharge)

        if (currency == null)
            currency = "BDT"
    }
}
