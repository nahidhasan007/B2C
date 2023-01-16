package net.sharetrip.hotel.booking.view.roomlist.adapter

import android.annotation.SuppressLint
import android.app.Dialog
import android.content.Context
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.view.Window
import android.widget.TextView
import androidx.databinding.DataBindingUtil
import androidx.recyclerview.widget.RecyclerView
import net.sharetrip.hotel.R
import net.sharetrip.hotel.booking.model.CancellationPolicy
import net.sharetrip.hotel.booking.model.Rate
import net.sharetrip.hotel.booking.model.RoomBasicInfo
import net.sharetrip.hotel.booking.view.roomlist.RoomItemNavigator
import net.sharetrip.hotel.booking.view.roomlist.RoomSelector
import net.sharetrip.hotel.booking.view.roomlist.RoomViewModel
import net.sharetrip.hotel.databinding.ItemRoomRateLayoutBinding

class RateAdapter(
    private val rateList: List<Rate>,
    private val navigator: RoomItemNavigator
) : RecyclerView.Adapter<RateAdapter.RateHolder>(), RoomSelector {
    var isExpand = false

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): RateHolder {
        val viewHolder =
            DataBindingUtil.inflate<ItemRoomRateLayoutBinding>(
                LayoutInflater.from(parent.context),
                R.layout.item_room_rate_layout, parent, false
            )
        return RateHolder(viewHolder)
    }

    override fun getItemCount(): Int = if (rateList.size > 2 && !isExpand) 2 else rateList.size

    @SuppressLint("NotifyDataSetChanged")
    override fun onBindViewHolder(holder: RateHolder, position: Int) {
        val rateItem = rateList[position]
        val viewModel = RoomViewModel(rateItem, this)
        holder.binding.viewModel = viewModel
        holder.binding.size = rateList.size
        holder.binding.roomCount = rateItem.roomQuantities.sumOf { it.quantity }
        holder.binding.position = position
        holder.binding.isExpand = isExpand
        holder.binding.root.setOnClickListener {
            isExpand = true
            notifyDataSetChanged()
        }
        holder.binding.cancellationPolicyText.setOnClickListener {
            showCancellationDialog(
                holder.binding.cancellationPolicyText.context,
                rateItem.cancellationPolicy
            )
        }
    }

    class RateHolder(val binding: ItemRoomRateLayoutBinding) : RecyclerView.ViewHolder(binding.root)

    override fun selectRoom(roomInfo: Rate) {
        val info = RoomBasicInfo()
        info.roomId = roomInfo.id
        info.perNightPrice = roomInfo.price.perNight
        info.perNightDiscountedPrice = roomInfo.price.discountedPerNight
        info.roomName = roomInfo.name
        info.gatewayType = roomInfo.gatewayCurrency
        info.providerCode = roomInfo.providerCode
        info.rooms = roomInfo.roomIds

        navigator.selectRoom(roomInfo.roomQuantities.sumOf { it.quantity }, info)
    }

    @SuppressLint("SetTextI18n")
    private fun showCancellationDialog(context: Context, cancellationPolicy: CancellationPolicy) {
        val dialog = Dialog(context, R.style.MyDynamicDialogTheme)
        dialog.requestWindowFeature(Window.FEATURE_NO_TITLE)
        dialog.setContentView(R.layout.dialog_cancellation)
        val refundableText = dialog.findViewById<TextView>(R.id.refundableText)
        val dateText = dialog.findViewById<TextView>(R.id.dateText)
        if (!cancellationPolicy.isNonRefundable) {
            refundableText.text = context.getString(R.string.refundable)
            dateText.visibility = View.VISIBLE
            cancellationPolicy.freeCancellationDate?.let {
                dateText.text =
                    context.getString(R.string.free_cancellation_date) + it.split("T")[0]
            }
        } else {
            refundableText.text = context.getString(R.string.non_refundable)
            dateText.visibility = View.GONE
        }
        dialog.show()
    }
}
