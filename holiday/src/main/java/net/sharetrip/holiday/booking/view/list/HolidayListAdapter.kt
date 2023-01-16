package net.sharetrip.holiday.booking.view.list

import android.text.SpannableString
import android.text.Spanned
import android.text.style.StrikethroughSpan
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.databinding.DataBindingUtil
import androidx.paging.PagingDataAdapter
import androidx.recyclerview.widget.RecyclerView
import com.example.holiday.R
import net.sharetrip.shared.utils.loadImage
import net.sharetrip.holiday.booking.model.HolidayDiscountType
import net.sharetrip.holiday.booking.model.HolidayItem
import net.sharetrip.shared.databinding.ItemHolidayBinding
import java.text.NumberFormat
import java.util.*

class HolidayListAdapter :
    PagingDataAdapter<HolidayItem, HolidayListAdapter.HolidayItemViewHolder>(HolidayItem.DIFF_CALLBACK) {

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): HolidayItemViewHolder {
        val binding = DataBindingUtil.inflate<ItemHolidayBinding>(
            LayoutInflater.from(parent.context),
            R.layout.item_holiday, parent, false
        )
        return HolidayItemViewHolder(binding)
    }

    override fun onBindViewHolder(holder: HolidayItemViewHolder, position: Int) {
        holder.bind(getItem(position)!!)
    }

    inner class HolidayItemViewHolder(private val tourBinding: ItemHolidayBinding) :
        RecyclerView.ViewHolder(tourBinding.root) {

        fun bind(item: HolidayItem) {
            tourBinding.textViewTime.text = "${item.duration}  Day"
            tourBinding.textViewTripCoin.text =
                NumberFormat.getNumberInstance(Locale.US).format(item.point.earningPoint)
            tourBinding.textViewPrice.text =
                item.currency + " " + NumberFormat.getNumberInstance(Locale.US)
                    .format(item.discountPrice)
            tourBinding.textViewDescription.text = item.title

            item.discount?.let {
                if (it > 0) {
                    val price = NumberFormat.getNumberInstance(Locale.US).format(item.lowestPrice)
                    val string = SpannableString("${item.currency} $price")
                    string.setSpan(
                        StrikethroughSpan(),
                        0,
                        string.length,
                        Spanned.SPAN_EXCLUSIVE_EXCLUSIVE
                    )
                    tourBinding.textViewPriceDiscounted.text = string

                    if (item.discountType.contentEquals(HolidayDiscountType.Percentage.toString())) {
                        val discountAmount = NumberFormat.getNumberInstance(Locale.US).format(it)
                        tourBinding.textViewDiscount.text = "*${discountAmount}%"
                    }
                } else {
                    tourBinding.textViewDiscount.visibility = View.GONE
                    tourBinding.textViewPriceDiscounted.visibility = View.GONE
                }
            }

            var location = ""
            for (i in item.locations?.indices!!) {
                location = if (i != item.locations?.size!! - 1) {
                    "$location${item.locations?.get(i)} - "
                } else {
                    "$location${item.locations?.get(i)}"
                }
            }

            tourBinding.textViewLocation.text = location

            if (item.withAirfare!!.endsWith("yes", true)) {
                tourBinding.textViewWitAirFair.visibility = View.VISIBLE
            } else {
                tourBinding.textViewWitAirFair.visibility = View.GONE
            }

            if (item.image.isNotEmpty()) {
                tourBinding.imageViewTrip.loadImage(item.image[0])
            }
        }
    }

}
