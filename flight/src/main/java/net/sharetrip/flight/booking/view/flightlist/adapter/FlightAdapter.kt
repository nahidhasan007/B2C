package net.sharetrip.flight.booking.view.flightlist.adapter

import android.annotation.SuppressLint
import android.graphics.Paint
import android.view.LayoutInflater
import android.view.View.GONE
import android.view.View.VISIBLE
import android.view.ViewGroup
import androidx.core.content.ContextCompat
import androidx.databinding.DataBindingUtil
import androidx.paging.PagingDataAdapter
import androidx.recyclerview.widget.DiffUtil
import androidx.recyclerview.widget.RecyclerView
import net.sharetrip.shared.utils.Strings
import net.sharetrip.flight.R
import net.sharetrip.flight.booking.model.flightresponse.flights.Flights
import net.sharetrip.flight.booking.view.flightlist.ItemFlightView
import net.sharetrip.flight.databinding.ItemFlightsBinding
import java.text.NumberFormat
import java.util.*

class FlightAdapter : PagingDataAdapter<Flights, RecyclerView.ViewHolder>(FlightDiffCallback) {

    override fun onCreateViewHolder(mPrent: ViewGroup, mViewType: Int): RecyclerView.ViewHolder {
        val context = mPrent.context
        val inflater = LayoutInflater.from(context)
        val flightsBinding = DataBindingUtil.inflate<ItemFlightsBinding>(
            inflater,
            R.layout.item_flights,
            mPrent,
            false
        )
        return FlightViewHolder(flightsBinding)
    }

    override fun onBindViewHolder(viewHolder: RecyclerView.ViewHolder, mPosition: Int) {
        (viewHolder as FlightViewHolder).bindTo(getItem(mPosition))
    }

    inner class FlightViewHolder(mItemView: ItemFlightsBinding) :
        RecyclerView.ViewHolder(mItemView.root) {
        private val bindingView: ItemFlightsBinding = mItemView

        @SuppressLint("SetTextI18n")
        fun bindTo(flights: Flights?) {
            val context = itemView.context
            val numberFormat = NumberFormat.getNumberInstance(Locale.US)
            val price =
                flights!!.currency + Strings.SPACE + numberFormat.format(flights.priceBreakdown.originPrice.toLong())
            bindingView.priceTextView.text = price
            bindingView.priceTextView.paintFlags = Paint.STRIKE_THRU_TEXT_FLAG
            val discountedPrice =
                flights.currency + Strings.SPACE + numberFormat.format(flights.priceBreakdown.promotionalAmount.toLong())
            bindingView.textViewDiscountedPrice.text = discountedPrice

            if (flights.priceBreakdown.promotionalDiscount == 0.0) {
                bindingView.priceTextView.visibility = GONE
            } else {
                bindingView.priceTextView.visibility = VISIBLE
                bindingView.priceTextView.textSize = 12f
            }

            val flightFlight = flights.flight
            val flightCount = flightFlight.size
            val childViewCount = bindingView.flightContainer.childCount
            val different = flightCount - childViewCount

            if (different == 0) {
                for (mIndex in 0 until flightCount) {
                    val mView = bindingView.flightContainer.getChildAt(mIndex) as ItemFlightView
                    mView.setItemFlight(flightFlight[mIndex])
                }
            }

            if (different > 0) {
                var index = 0
                while (index < childViewCount) {
                    val mView = bindingView.flightContainer.getChildAt(index) as ItemFlightView
                    mView.setItemFlight(flightFlight[index])
                    index++
                }
                while (index < flightCount) {
                    val mFlight = flightFlight[index]
                    val mFlightView = ItemFlightView(context)
                    mFlightView.setItemFlight(mFlight)
                    bindingView.flightContainer.addView(mFlightView)
                    index++
                }
            } else if (different < 0) {
                var index = 0
                while (index < flightCount) {
                    val flightView = bindingView.flightContainer.getChildAt(index) as ItemFlightView
                    flightView.setItemFlight(flightFlight[index])
                    index++
                }
                while (index < flightCount) {
                    bindingView.flightContainer.removeViewAt(index)
                    index++
                }
            }

            bindingView.tripCoinTextView.text = numberFormat.format(flights.earnPoint.toLong())
            bindingView.textviewRefundableStatus.text = flights.isRefundable
            bindingView.flightDeal.visibility = VISIBLE

            when (flights.deal) {
                "Preferred" -> {
                    bindingView.flightDeal.background =
                        ContextCompat.getDrawable(context, R.drawable.flight_deal_blue_background)
                    bindingView.flightDeal.text = "Preferred Airlines"
                }
                "Best Deal" -> {
                    bindingView.flightDeal.background =
                        ContextCompat.getDrawable(context, R.drawable.flight_deal_red_background)
                    bindingView.flightDeal.text = "Best Deal"
                }
                else -> bindingView.flightDeal.visibility = GONE
            }
        }
    }

    companion object {
        private val FlightDiffCallback: DiffUtil.ItemCallback<Flights> =
            object : DiffUtil.ItemCallback<Flights>() {
                override fun areItemsTheSame(oldItem: Flights, newItem: Flights): Boolean {
                    return oldItem.sequence == newItem.sequence
                }

                override fun areContentsTheSame(oldItem: Flights, newItem: Flights): Boolean {
                    return oldItem == newItem
                }
            }
    }
}
