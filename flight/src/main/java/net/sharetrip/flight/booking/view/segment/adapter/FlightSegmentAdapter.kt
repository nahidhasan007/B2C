package net.sharetrip.flight.booking.view.segment.adapter

import android.annotation.SuppressLint
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.databinding.DataBindingUtil
import androidx.lifecycle.MutableLiveData
import androidx.recyclerview.widget.RecyclerView
import com.bumptech.glide.Glide
import com.bumptech.glide.load.resource.bitmap.CircleCrop
import com.bumptech.glide.request.RequestOptions
import net.sharetrip.shared.view.adapter.BaseRecyclerAdapter
import net.sharetrip.flight.R
import net.sharetrip.flight.databinding.ItemDetailOfFlightSegmentBinding
import net.sharetrip.flight.databinding.ItemSegmentDetailOfFlightBinding
import net.sharetrip.flight.booking.model.FLIGHT
import net.sharetrip.flight.booking.model.SEGMENT
import net.sharetrip.flight.booking.model.flightresponse.flights.flight.Flight
import net.sharetrip.flight.booking.model.flightresponse.flights.segment.Segment

class FlightSegmentAdapter : BaseRecyclerAdapter<Any?, RecyclerView.ViewHolder?>() {
    private val isBackPressed = MutableLiveData<Boolean>()
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
            val binding = DataBindingUtil.inflate<ItemSegmentDetailOfFlightBinding>(
                inflater,
                R.layout.item_segment_detail_of_flight,
                parent,
                false
            )
            FlightViewHolder(binding)
        } else {
            val binding = DataBindingUtil.inflate<ItemDetailOfFlightSegmentBinding>(
                inflater,
                R.layout.item_detail_of_flight_segment,
                parent,
                false
            )
            SegmentViewHolder(binding)
        }
        return viewHolder
    }

    @SuppressLint("SetTextI18n")
    override fun onBindViewHolder(holder: RecyclerView.ViewHolder, position: Int) {
        if (FLIGHT == holder.itemViewType) {
            if (holder is FlightViewHolder) {
                val item = getItem(position) as Flight?
                holder.mBinding.flight = item
                holder.mBinding.imageClose.setOnClickListener { isBackPressed.setValue(true) }
            }
        } else if (SEGMENT == holder.itemViewType) {
            val mSegment = getItem(position) as Segment?
            val mSegmentViewHolder = holder as SegmentViewHolder
            mSegmentViewHolder.binding.segment = mSegment
            Glide.with(holder.itemView.context)
                .load(mSegment!!.logo)
                .apply(RequestOptions.bitmapTransform(CircleCrop()))
                .into(mSegmentViewHolder.binding.flightIcon)

            if (mSegment.hiddenStop?.code == null) {
                holder.binding.linearTechnicalStoppage.visibility = View.GONE
            } else {
                holder.binding.linearTechnicalStoppage.visibility = View.VISIBLE

                holder.binding.tvTechinicalStoppageExplanasion.text =
                    "Note: This flight has technical stoppage at " +
                            mSegment.hiddenStop!!.city + ", " + mSegment.hiddenStop!!.airport + " (" + mSegment.hiddenStop!!.code + "), Before booking this flight, please check you visa requirement as per your Nationality by mailing at flight@sharetrip.net."
            }

            if (mSegment.transitText() != 0) {
                holder.binding.linearTransit.visibility = View.VISIBLE
                holder.binding.tvTransit.setText(mSegment.transitText())
            }

        }
    }

    fun onBackPressed(): MutableLiveData<Boolean> {
        return isBackPressed
    }

    internal inner class FlightViewHolder(val mBinding: ItemSegmentDetailOfFlightBinding) :
        RecyclerView.ViewHolder(mBinding.root)

    internal inner class SegmentViewHolder(val binding: ItemDetailOfFlightSegmentBinding) :
        RecyclerView.ViewHolder(binding.root)
}