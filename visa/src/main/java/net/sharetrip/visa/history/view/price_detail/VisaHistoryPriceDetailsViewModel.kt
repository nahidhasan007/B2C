package net.sharetrip.visa.history.view.price_detail

import androidx.databinding.ObservableField
import com.sharetrip.base.viewmodel.BaseViewModel
import net.sharetrip.visa.history.model.VisaProductSnapshot
import java.text.NumberFormat
import java.util.*

class VisaHistoryPriceDetailsViewModel(
    private val product: VisaProductSnapshot,
    val travellerCount: Int,
    val convenienceFee: Int,
    val vatOnConvenienceFee: Int
) : BaseViewModel() {

    val totalPayableAmount = ObservableField<String>()
    val discount = ObservableField<String>()
    val totalAmount = ObservableField<String>()
    val travellerTitle = ObservableField<String>()
    val visaFeeTitle = ObservableField<String>()
    val visaFeeValueText = ObservableField<String>()
    val processingFeeTitle = ObservableField<String>()
    val processingFeeValueText = ObservableField<String>()
    private var totalPayable = 0.0
    private var total = 0.0
    val isConvenienceVisible = convenienceFee > 0

    init {
        calculatePrice()
    }

    private fun calculatePrice() {
        val visaFee = product.visaFee?.times(travellerCount)
        visaFeeValueText.set(NumberFormat.getNumberInstance(Locale.US).format(visaFee))

        val processFee = product.processingFee?.times(travellerCount)
        processingFeeValueText.set(NumberFormat.getNumberInstance(Locale.US).format(processFee))

        total = visaFee!! + processFee!!
        totalAmount.set(NumberFormat.getNumberInstance(Locale.US).format(total))

        travellerTitle.set("Travelers x $travellerCount")
        visaFeeTitle.set("Visa Fee  x $travellerCount")

        val processingTitle = "Share Trip Processing  Fee x $travellerCount"
        processingFeeTitle.set(processingTitle)

        totalPayable = total - product.discount!!
        totalPayableAmount.set(
            "BDT ${
                NumberFormat.getNumberInstance(Locale.US)
                    .format(totalPayable + convenienceFee + vatOnConvenienceFee)
            }"
        )
        discount.set(NumberFormat.getNumberInstance(Locale.US).format(product.discount))
    }

}