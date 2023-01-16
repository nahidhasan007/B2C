package net.sharetrip.hotel.history.view.historylist

import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.core.content.ContextCompat
import androidx.paging.PagingDataAdapter
import androidx.recyclerview.widget.DiffUtil
import androidx.recyclerview.widget.RecyclerView
import net.sharetrip.hotel.R
import net.sharetrip.hotel.databinding.ItemHotelHistoryBinding
import net.sharetrip.hotel.history.model.HotelHistoryItem

class HotelHistoryListAdapter :
    PagingDataAdapter<HotelHistoryItem, HotelHistoryListAdapter.HistoryViewHolder>(
        HotelHistoryDiffCallback) {

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): HistoryViewHolder {
        val layoutInflater = LayoutInflater.from(parent.context)
        val bindingView = ItemHotelHistoryBinding.inflate(layoutInflater, parent, false)
        return HistoryViewHolder(bindingView)
    }

    override fun onBindViewHolder(holder: HistoryViewHolder, position: Int) {
        try {
            val item = getItem(position)
            if (item!!.hotel == null) return

            holder.itemHotelHistoryBinding.item = item

            var adult = 0
            var child = 0
            for ((_, _, adults, _, children) in item.bookedRooms) {
                adult += adults
                child += children
            }
            holder.itemHotelHistoryBinding.textViewTotalGuest.text = "$adult Adult $child Child"

            val status = item.status
            when {
                status.contains("CANCELLED") ->
                    holder.itemHotelHistoryBinding.textViewStatus.setTextColor(
                        ContextCompat.getColor(
                            holder.itemHotelHistoryBinding.textViewStatus.context,
                            R.color.error_color
                        )
                    )

                status.contains("WAITING") ->
                    holder.itemHotelHistoryBinding.textViewStatus.setTextColor(
                        ContextCompat.getColor(
                            holder.itemHotelHistoryBinding.textViewStatus.context,
                            R.color.orange_light
                        )
                    )

                else ->
                    holder.itemHotelHistoryBinding.textViewStatus.setTextColor(
                        ContextCompat.getColor(
                            holder.itemHotelHistoryBinding.textViewStatus.context,
                            R.color.mid_green
                        )
                    )
            }
        } catch (e: Exception) {
        }
    }

    fun getItemAtAdapterPosition(position: Int): HotelHistoryItem? {
        return getItem(position)
    }

    inner class HistoryViewHolder(val itemHotelHistoryBinding: ItemHotelHistoryBinding) :
        RecyclerView.ViewHolder(itemHotelHistoryBinding.root)

    companion object {
        val HotelHistoryDiffCallback: DiffUtil.ItemCallback<HotelHistoryItem> =
            object : DiffUtil.ItemCallback<HotelHistoryItem>() {
                override fun areItemsTheSame(
                    oldItem: HotelHistoryItem,
                    newItem: HotelHistoryItem,
                ): Boolean {
                    return oldItem.voucherId === oldItem.voucherId
                }

                override fun areContentsTheSame(
                    oldItem: HotelHistoryItem,
                    newItem: HotelHistoryItem,
                ): Boolean {
                    return oldItem == newItem
                }
            }
    }
}
