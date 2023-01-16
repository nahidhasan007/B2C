package net.sharetrip.visa.booking.view.home

import android.annotation.SuppressLint
import android.text.SpannableString
import android.text.Spanned
import android.text.style.StrikethroughSpan
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.databinding.DataBindingUtil
import androidx.paging.PagingDataAdapter
import androidx.recyclerview.widget.RecyclerView
import net.sharetrip.shared.utils.loadImage
import net.sharetrip.visa.R
import net.sharetrip.visa.booking.model.VisaDiscountType
import net.sharetrip.visa.booking.model.VisaItem
import net.sharetrip.visa.databinding.ItemVisaCountryBinding
import java.text.NumberFormat
import java.util.*

class VisaAdapter
    : PagingDataAdapter<VisaItem, VisaAdapter.VisaItemViewHolder>(VisaItem.DIFF_CALLBACK) {

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): VisaItemViewHolder {
        val binding = DataBindingUtil.inflate<ItemVisaCountryBinding>(
            LayoutInflater.from(parent.context),
            R.layout.item_visa_country, parent, false
        )

        return VisaItemViewHolder(binding)
    }

    override fun onBindViewHolder(holder: VisaItemViewHolder, position: Int) {
        val visaItem = getItem(position)!!
        holder.bind(visaItem)
    }

    inner class VisaItemViewHolder(private val visaBinding: ItemVisaCountryBinding) :
        RecyclerView.ViewHolder(visaBinding.root) {
        @SuppressLint("SetTextI18n")
        fun bind(item: VisaItem) {
            visaBinding.tvCountryName.text = item.countryName
            visaBinding.tvVisaReq.text = item.visaRequirement
            visaBinding.tvVisaCurrency.text = item.currency
            visaBinding.tvTimeZone.text = item.localTime
            visaBinding.tvEarnCoin.text = "" + item.points?.earning
            visaBinding.ivVisaCountry.loadImage(item.photos?.get(0) ?: "")
            val price = item.bookingCurrency + " " + NumberFormat.getNumberInstance(Locale.US)
                .format(item.discountPrice)
            visaBinding.tvVisaPrice.text = price
            item.discount.let {
                if (it > 0) {
                    val string = SpannableString("${item.bookingCurrency} ${item.lowestPrice}")
                    string.setSpan(
                        StrikethroughSpan(),
                        0,
                        string.length,
                        Spanned.SPAN_EXCLUSIVE_EXCLUSIVE
                    )
                    visaBinding.tvOldPrice.text = string

                    if (item.discountType.contentEquals(VisaDiscountType.Flat.toString())) {
                        val discountAmount =
                            NumberFormat.getNumberInstance(Locale.US).format(item.discount)
                        visaBinding.tvVisaDiscount.text =
                            "${item.bookingCurrency} $discountAmount OFF"
                    } else if (item.discountType.contentEquals(VisaDiscountType.Percentage.toString())) {
                        val discountAmount =
                            NumberFormat.getNumberInstance(Locale.US).format(item.discount)
                        visaBinding.tvVisaDiscount.text = "${discountAmount}% OFF"
                    }
                } else {
                    visaBinding.tvVisaDiscount.visibility = View.GONE
                    visaBinding.tvOldPrice.visibility = View.GONE
                }
            }
        }
    }
}
