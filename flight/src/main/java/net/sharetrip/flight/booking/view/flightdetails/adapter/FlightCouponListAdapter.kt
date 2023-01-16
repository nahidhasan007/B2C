package net.sharetrip.flight.booking.view.flightdetails.adapter

import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.databinding.DataBindingUtil
import androidx.recyclerview.widget.RecyclerView
import net.sharetrip.flight.R
import net.sharetrip.flight.booking.model.PromotionalCoupon
import net.sharetrip.flight.booking.view.flightbookingsummary.FlightSummaryViewModel
import net.sharetrip.flight.booking.view.flightdetails.FlightDetailsViewModel
import net.sharetrip.flight.databinding.ItemCouponFlightBinding


class FlightCouponListAdapter(
    private val viewModel: Any,
    private val flightCouponList: MutableList<PromotionalCoupon> = mutableListOf(),
    private var selectedCoupon: String = ""
) : RecyclerView.Adapter<FlightCouponListAdapter.CouponViewHolder>() {
    var selectedPosition = -1

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): CouponViewHolder {
        val viewHolder = DataBindingUtil.inflate<ItemCouponFlightBinding>(
            LayoutInflater.from(parent.context),
            R.layout.item_coupon_flight, parent, false
        )
        return CouponViewHolder(viewHolder)
    }

    override fun onBindViewHolder(holder: CouponViewHolder, position: Int) {
        holder.binding.coupon = flightCouponList[position]

        if (position == selectedPosition) {
            if (flightCouponList[position].isSelected) {
                flightCouponList[position].isSelected = false

                if (viewModel is FlightSummaryViewModel) {
                    viewModel.setCoupon("")
                } else if (viewModel is FlightDetailsViewModel) {
                    viewModel.setCoupon("")
                }
            } else {
                flightCouponList[position].isSelected = true

                if (viewModel is FlightSummaryViewModel) {
                    viewModel.setCoupon(flightCouponList[position].coupon)
                } else if (viewModel is FlightDetailsViewModel) {
                    viewModel.setCoupon(flightCouponList[position].coupon)
                }
            }
        } else {
            if (selectedCoupon == flightCouponList[position].coupon) {
                selectedPosition = position
                selectedCoupon = ""
                flightCouponList[position].isSelected = true
            } else {
                flightCouponList[position].isSelected = false
            }
        }

        holder.binding.root.setOnClickListener {
            val previousSelectedPosition = selectedPosition
            selectedPosition = position
            notifyItemChanged(previousSelectedPosition)
            notifyItemChanged(position)
        }
    }

//    fun addCouponList(list: List<PromotionalCoupon>) {
//        flightCouponList.clear()
//        flightCouponList.addAll(list)
//        notifyDataSetChanged()
//    }

    override fun getItemCount() = flightCouponList.size

    inner class CouponViewHolder(val binding: ItemCouponFlightBinding) :
        RecyclerView.ViewHolder(binding.root)
}