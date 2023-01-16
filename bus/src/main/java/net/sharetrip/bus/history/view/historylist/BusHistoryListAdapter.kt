package net.sharetrip.bus.history.view.historylist

import android.graphics.Color
import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.paging.PagingDataAdapter
import androidx.recyclerview.widget.DiffUtil
import androidx.recyclerview.widget.RecyclerView
import net.sharetrip.bus.databinding.BusHistoryTripHistoryViewBinding
import net.sharetrip.bus.history.model.BookingStatus
import net.sharetrip.bus.history.model.HistoryDetails

class BusHistoryListAdapter :
    PagingDataAdapter<HistoryDetails, BusHistoryListAdapter.ViewHolder>(BusHistoryDiffCallback) {

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
        val lf: LayoutInflater = LayoutInflater.from(parent.context)
        val binding = BusHistoryTripHistoryViewBinding.inflate(lf, parent, false)
        return ViewHolder(binding)
    }

    override fun onBindViewHolder(holder: ViewHolder, position: Int) {
        val item = getItem(position)
        item?.let {
            holder.itemViewBinding.model = item
            holder.itemViewBinding.status = getStatus(item.bookingStatus)
            holder.itemViewBinding.statusColor =
                Color.parseColor(getColor(item.bookingStatus))
        }
    }

    inner class ViewHolder(val itemViewBinding: BusHistoryTripHistoryViewBinding) :
        RecyclerView.ViewHolder(itemViewBinding.root)

    companion object {
        fun getColor(status: Int): String {
            when (status) {
                BookingStatus.BOOKED.value -> return "#1882FF"
                BookingStatus.CONFIRMED.value -> return "#43A046"
                BookingStatus.CANCELED.value, BookingStatus.FAILED.value -> return "#FF3326"
            }
            return "#43A046"
        }

        fun getStatus(status: Int): String {
            when (status) {
                BookingStatus.BOOKED.value -> return "BOOKED"
                BookingStatus.CANCELED.value -> return "CANCELED"
                BookingStatus.CONFIRMED.value -> return "CONFIRMED"
                BookingStatus.FAILED.value -> return "FAILED"
            }
            return "NONE"
        }

        val BusHistoryDiffCallback: DiffUtil.ItemCallback<HistoryDetails> =
            object : DiffUtil.ItemCallback<HistoryDetails>() {
                override fun areItemsTheSame(
                    oldItem: HistoryDetails,
                    newItem: HistoryDetails,
                ): Boolean {
                    return oldItem.bookingId === oldItem.bookingId
                }

                override fun areContentsTheSame(
                    oldItem: HistoryDetails,
                    newItem: HistoryDetails,
                ): Boolean {
                    return oldItem == newItem
                }
            }
    }
}
