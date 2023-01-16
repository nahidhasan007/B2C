package net.sharetrip.flight.booking.view.flightdetails.adapter

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.databinding.DataBindingUtil
import androidx.recyclerview.widget.RecyclerView
import com.bumptech.glide.Glide
import com.bumptech.glide.load.resource.bitmap.CircleCrop
import com.bumptech.glide.request.RequestOptions
import net.sharetrip.shared.view.adapter.BaseRecyclerAdapter
import net.sharetrip.flight.R
import net.sharetrip.flight.booking.view.flightdetails.FlightDetailsViewModel
import net.sharetrip.flight.databinding.ItemDetailOfFlightBinding
import net.sharetrip.flight.databinding.ItemDetailOfFlightSegmentBinding
import net.sharetrip.flight.booking.model.FLIGHT
import net.sharetrip.flight.booking.model.SEGMENT
import net.sharetrip.flight.booking.model.flightresponse.flights.flight.Flight
import net.sharetrip.flight.booking.model.flightresponse.flights.segment.Segment

class FlightDetailsAdapter(private val flightDetailsViewModel: FlightDetailsViewModel) : BaseRecyclerAdapter<Any?, RecyclerView.ViewHolder?>() {

    override fun getItemViewType(position: Int): Int {
        return when (getItem(position)) {
            is Flight -> {
                FLIGHT
            }
            is Segment -> {
                SEGMENT
            }
            else -> {
                -1
            }
        }
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): RecyclerView.ViewHolder {
        val viewHolder: RecyclerView.ViewHolder
        val inflater = LayoutInflater.from(parent.context)
        viewHolder = if (FLIGHT == viewType) {
            val binding = DataBindingUtil.inflate<ItemDetailOfFlightBinding>(inflater, R.layout.item_detail_of_flight, parent, false)
            FlightViewHolder(binding)
        } else {
            val binding = DataBindingUtil.inflate<ItemDetailOfFlightSegmentBinding>(inflater, R.layout.item_detail_of_flight_segment, parent, false)
            SegmentViewHolder(binding)
        }
        return viewHolder
    }

    override fun onBindViewHolder(holder: RecyclerView.ViewHolder, position: Int) {
        if (FLIGHT == holder.itemViewType) {
            if (holder is FlightViewHolder) {
                val item = getItem(position) as Flight?
                holder.mBinding.flight = item
                Glide.with(holder.mBinding.flightLogo.context)
                        .load(item!!.logo)
                        .apply(RequestOptions.bitmapTransform(CircleCrop()))
                        .into(holder.mBinding.flightLogo)
                holder.mBinding.textSeeDetails.setOnClickListener { flightDetailsViewModel.gotoSegmentFragment(position) }

                if (item.hiddenStops) {
                    holder.mBinding.relativeTechnicalStoppage.visibility = View.VISIBLE
                } else {
                    holder.mBinding.relativeTechnicalStoppage.visibility = View.GONE
                }
            }
        } else if (SEGMENT == holder.itemViewType) {
            val mSegment = getItem(position) as Segment?
            val mSegmentViewHolder = holder as SegmentViewHolder
            mSegmentViewHolder.binding.segment = mSegment
        }
    }

    internal inner class FlightViewHolder(val mBinding: ItemDetailOfFlightBinding) : RecyclerView.ViewHolder(mBinding.root)

    internal inner class SegmentViewHolder(val binding: ItemDetailOfFlightSegmentBinding) : RecyclerView.ViewHolder(binding.root)

}