package net.sharetrip.visa.utils

import android.text.Layout
import android.text.style.AlignmentSpan
import net.sharetrip.shared.utils.Strings
import net.sharetrip.shared.utils.span.LineOverlapSpan
import net.sharetrip.shared.utils.span.Truss
import net.sharetrip.visa.booking.model.price.PriceBreakdown
import java.text.NumberFormat
import java.util.*

object PriceBreakDownUtil {

    @JvmStatic
    fun getFormattedPriceBreakDown(mPriceBreakdown: PriceBreakdown?): CharSequence {
        val priceTruss = Truss()
        val details = mPriceBreakdown?.details
        val numberFormat = NumberFormat.getNumberInstance(Locale.US)
        if (details != null) {
            for (detail in details) {
                if (detail.numberPaxes == "0") continue
                val type = detail.type.lowercase()
                priceTruss.append(
                    type.substring(0, 1)
                        .uppercase() + type.substring(1) + " * " + detail.numberPaxes
                )
                priceTruss.append(Strings.LINE_BREAK)
                priceTruss.append("Base Fare * " + detail.numberPaxes)
                priceTruss.pushSpan(LineOverlapSpan())
                priceTruss.append(Strings.LINE_BREAK)
                priceTruss.popSpan()
                priceTruss.pushSpan(AlignmentSpan.Standard(Layout.Alignment.ALIGN_OPPOSITE))
                val baseFare = detail.baseFare.toDouble() * detail.numberPaxes.toDouble()
                priceTruss.append(numberFormat.format(baseFare.toLong()))
                priceTruss.popSpan()
                priceTruss.append(Strings.LINE_BREAK)
                priceTruss.append("Taxes & Fees * " + detail.numberPaxes)
                priceTruss.pushSpan(LineOverlapSpan())
                priceTruss.append(Strings.LINE_BREAK)
                priceTruss.popSpan()
                priceTruss.pushSpan(AlignmentSpan.Standard(Layout.Alignment.ALIGN_OPPOSITE))
                val tax = detail.tax.toDouble() * detail.numberPaxes.toDouble()
                priceTruss.append(numberFormat.format(tax.toLong()))
                priceTruss.popSpan()
                priceTruss.append(Strings.LINE_BREAK)
                priceTruss.append(Strings.LINE_BREAK)
            }
        }
        return priceTruss.build()
    }
}
