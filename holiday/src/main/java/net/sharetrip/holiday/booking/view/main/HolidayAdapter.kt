package net.sharetrip.holiday.booking.view.main

import android.annotation.SuppressLint
import android.content.Context
import android.text.SpannableString
import android.text.Spanned
import android.text.style.StrikethroughSpan
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.databinding.DataBindingUtil
import androidx.paging.PagingDataAdapter
import androidx.recyclerview.widget.RecyclerView
import com.example.holiday.R
import com.example.holiday.databinding.ItemHolidayHeaderBinding
import com.example.holiday.databinding.ItemOfHolidayBinding
import com.sharetrip.base.event.EventObserver
import com.sharetrip.base.network.NetworkUtil
import net.sharetrip.shared.utils.loadImage
import net.sharetrip.holiday.booking.model.HolidayDiscountType
import net.sharetrip.holiday.booking.model.HolidayItem
import net.sharetrip.shared.view.adapter.ItemClickSupport
import java.text.NumberFormat
import java.util.*

class HolidayAdapter(private val holidayHeaderViewModel: HolidayHeaderViewModel) :
    PagingDataAdapter<HolidayItem, RecyclerView.ViewHolder>(HolidayItem.DIFF_CALLBACK) {

    private lateinit var context: Context

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): RecyclerView.ViewHolder {
        context = parent.context

        if (viewType == VIEW_TYPE_ITEM) {
            val binding = DataBindingUtil.inflate<ItemOfHolidayBinding>(
                LayoutInflater.from(parent.context),
                R.layout.item_of_holiday, parent, false
            )
            return HolidayItemViewHolder(binding)
        }

        val binding = DataBindingUtil.inflate<ItemHolidayHeaderBinding>(
            LayoutInflater.from(parent.context),
            R.layout.item_holiday_header, parent, false
        )

        return HolidayHeaderViewHolder(binding)
    }

    override fun onBindViewHolder(holder: RecyclerView.ViewHolder, position: Int) {
        if (holder is HolidayItemViewHolder) {
            holder.bind(getItem(position - 1)!!)
        } else if (holder is HolidayHeaderViewHolder) {
            holder.showData(holidayHeaderViewModel)
        }
    }

    override fun getItemViewType(position: Int): Int {
        if (position == 0)
            return VIEW_TYPE_HEADER
        return VIEW_TYPE_ITEM
    }

    inner class HolidayItemViewHolder(private val holidayBinding: ItemOfHolidayBinding) :
        RecyclerView.ViewHolder(holidayBinding.root) {

        @SuppressLint("SetTextI18n")
        fun bind(item: HolidayItem) {
            holidayBinding.textViewTime.text = item.duration.toString() + " Day"
            holidayBinding.textViewTripCoin.text =
                NumberFormat.getNumberInstance(Locale.US).format(item.point.earningPoint)
            holidayBinding.textViewPrice.text =
                item.currency + " " + NumberFormat.getNumberInstance(Locale.US)
                    .format(item.discountPrice)
            holidayBinding.textViewDescription.text = item.title

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
                    holidayBinding.textViewPriceDiscounted.text = string

                    if (item.discountType.contentEquals(HolidayDiscountType.Percentage.toString())) {
                        val discountAmount = NumberFormat.getNumberInstance(Locale.US).format(it)
                        holidayBinding.textViewDiscount.text = "*${discountAmount}%"
                    }
                } else {
                    holidayBinding.textViewDiscount.visibility = View.GONE
                    holidayBinding.textViewPriceDiscounted.visibility = View.GONE
                }
            }

            var location = ""

            for (i in item.locations!!.indices) {
                location = if (i != item.locations!!.size - 1) {
                    "$location${item.locations!![i]} - "
                } else {
                    "$location${item.locations!![i]}"
                }
            }

            holidayBinding.textViewLocation.text = location

            if (item.withAirfare!!.endsWith("yes", true)) {
                holidayBinding.textViewWitAirFair.visibility = View.VISIBLE
            } else {
                holidayBinding.textViewWitAirFair.visibility = View.GONE
            }

            if (item.image.isNotEmpty()) {
                holidayBinding.imageViewTrip.loadImage(item.image[0])
            }
        }
    }

    inner class HolidayHeaderViewHolder(private val holidayHeader: ItemHolidayHeaderBinding) :
        RecyclerView.ViewHolder(holidayHeader.root) {

        private val cityAdapter = HolidayCityAdapter()
        private val destinationAdapter = DestinationAdapter(holidayHeaderViewModel)

        fun showData(viewModel: HolidayHeaderViewModel) {
            holidayHeader.viewModel = viewModel
            holidayHeader.listPopularCities.adapter = cityAdapter
            holidayHeader.recyclerDestinations.adapter = destinationAdapter

            viewModel.liveCities.observeForever { tourCities ->
                cityAdapter.update(tourCities)
            }

            viewModel.liveDestination.observeForever {
                destinationAdapter.update(it)
            }

            viewModel.showToast.observeForever(EventObserver{
                Toast.makeText(context, it, Toast.LENGTH_SHORT).show()
            })

            viewModel.initDestinationAdapter()

            ItemClickSupport.addTo(holidayHeader.listPopularCities)
                .setOnItemClickListener { _, position, _ ->
                    if (NetworkUtil.hasNetwork(context))
                        viewModel.navigateToCityHolidayList(position)
                    else
                        Toast.makeText(context, "No internet", Toast.LENGTH_LONG).show()
                }
        }
    }

    companion object {
        private const val VIEW_TYPE_HEADER = 0
        private const val VIEW_TYPE_ITEM = 1
    }
}
