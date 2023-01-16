package net.sharetrip.hotel.booking.view.summary

import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.databinding.DataBindingUtil
import androidx.recyclerview.widget.RecyclerView
import net.sharetrip.hotel.R
import net.sharetrip.hotel.booking.model.PromotionalCoupon
import net.sharetrip.hotel.booking.view.hoteldiscount.HotelDiscountViewModel
import net.sharetrip.hotel.databinding.ItemHotelCouponBinding

class HotelCouponListAdapter(
    private val viewModel: Any,
    private val couponListener: (PromotionalCoupon) -> Unit,
    private val hotelCouponList: ArrayList<PromotionalCoupon> = arrayListOf(),
    private var selectedCoupon: String = ""
) : RecyclerView.Adapter<HotelCouponListAdapter.CouponViewHolder>() {
    var selectedPosition = -1

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): CouponViewHolder {
        val viewHolder = DataBindingUtil.inflate<ItemHotelCouponBinding>(
            LayoutInflater.from(parent.context),
            R.layout.item_hotel_coupon, parent, false
        )
        return CouponViewHolder(viewHolder)
    }

    override fun onBindViewHolder(holder: CouponViewHolder, position: Int) {
        holder.binding.coupon = hotelCouponList[position]

        if (position == selectedPosition) {
            if (hotelCouponList[position].isSelected) {
                hotelCouponList[position].isSelected = false

                if (viewModel is HotelDiscountViewModel) {
                    viewModel.setCoupon("")
                } else if (viewModel is BookingSummaryViewModel) {
                    viewModel.setCoupon("")
                }
            } else {
                hotelCouponList[position].isSelected = true

                if (viewModel is HotelDiscountViewModel) {
                    viewModel.setCoupon(hotelCouponList[position].coupon)
                } else if (viewModel is BookingSummaryViewModel) {
                    viewModel.setCoupon(hotelCouponList[position].coupon)
                }
            }

            couponListener.invoke(hotelCouponList[position])
        } else {
            if (selectedCoupon == hotelCouponList[position].coupon) {
                selectedPosition = position
                selectedCoupon = ""
                hotelCouponList[position].isSelected = true
            } else {
                hotelCouponList[position].isSelected = false
            }
        }

        holder.binding.root.setOnClickListener {
            val previousSelectedPosition = selectedPosition
            selectedPosition = position
            notifyItemChanged(previousSelectedPosition)
            notifyItemChanged(position)
        }
    }

    fun clearSelection() {
        if (selectedPosition >= 0) {
            notifyItemChanged(selectedPosition)
            selectedPosition = -1
        }
    }

    override fun getItemCount() = hotelCouponList.size

    inner class CouponViewHolder(val binding: ItemHotelCouponBinding) :
        RecyclerView.ViewHolder(binding.root)
}
