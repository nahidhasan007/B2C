package net.sharetrip.flight.history.model

import android.os.Parcelable
import kotlinx.parcelize.Parcelize

@Parcelize
data class VoidQuotationResponse(
    val airlineVoidCharge: Int,
    val automationType: Boolean,
    val confirmationTime: Int,
    val msg: String,
    val purchasePrice: Int,
    val reQuotationTime: String,
    val stVoidCharge: Int,
    val status: String,
    val totalFee: Int,
    val totalReturnAmount: Int,
    val voidSearchId: String
):Parcelable