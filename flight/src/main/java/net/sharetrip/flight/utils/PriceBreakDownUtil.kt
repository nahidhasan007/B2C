package net.sharetrip.flight.utils

import android.text.Layout
import android.text.style.AlignmentSpan
import net.sharetrip.shared.utils.span.LineOverlapSpan
import net.sharetrip.shared.utils.span.Truss
import net.sharetrip.shared.utils.Strings
import net.sharetrip.flight.booking.model.flightresponse.flights.price.PriceBreakdown
import java.text.NumberFormat
import java.util.*

object PriceBreakDownUtil {

    @JvmStatic
    fun getFormattedPriceBreakDown(mPriceBreakdown: PriceBreakdown?): CharSequence {
        val mPriceTruss = Truss()
        val details = mPriceBreakdown?.details
        val numberFormat = NumberFormat.getNumberInstance(Locale.US)
        if (details != null) {
            for (mDetail in details) {
                if (mDetail.numberPaxes == "0") continue
                val type = mDetail.type.lowercase()
                mPriceTruss.append(type.substring(0, 1).uppercase() + type.substring(1) + " * " + mDetail.numberPaxes)
                mPriceTruss.append(Strings.LINE_BREAK)
                mPriceTruss.append("Base Fare * " + mDetail.numberPaxes)
                mPriceTruss.pushSpan(LineOverlapSpan())
                mPriceTruss.append(Strings.LINE_BREAK)
                mPriceTruss.popSpan()
                mPriceTruss.pushSpan(AlignmentSpan.Standard(Layout.Alignment.ALIGN_OPPOSITE))
                val basefare = mDetail.baseFare.toDouble() * mDetail.numberPaxes.toDouble()
                mPriceTruss.append(numberFormat.format(basefare.toLong()))
                mPriceTruss.popSpan()
                mPriceTruss.append(Strings.LINE_BREAK)
                mPriceTruss.append("Taxes & Fees * " + mDetail.numberPaxes)
                mPriceTruss.pushSpan(LineOverlapSpan())
                mPriceTruss.append(Strings.LINE_BREAK)
                mPriceTruss.popSpan()
                mPriceTruss.pushSpan(AlignmentSpan.Standard(Layout.Alignment.ALIGN_OPPOSITE))
                val tax = mDetail.tax.toDouble() * mDetail.numberPaxes.toDouble()
                mPriceTruss.append(numberFormat.format(tax.toLong()))
                mPriceTruss.popSpan()
                mPriceTruss.append(Strings.LINE_BREAK)
                mPriceTruss.append(Strings.LINE_BREAK)
            }
        }
        return mPriceTruss.build()
    }
}