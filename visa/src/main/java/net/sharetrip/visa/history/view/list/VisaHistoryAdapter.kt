package net.sharetrip.visa.history.view.list

import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.core.content.ContextCompat
import androidx.databinding.DataBindingUtil
import androidx.paging.PagingDataAdapter
import androidx.recyclerview.widget.DiffUtil
import androidx.recyclerview.widget.RecyclerView
import net.sharetrip.visa.R
import net.sharetrip.visa.databinding.ItemVisaHistoryBinding
import net.sharetrip.visa.history.model.VisaBookingStatus
import net.sharetrip.visa.history.model.VisaHistoryItem
import net.sharetrip.visa.history.model.VisaPaymentStatus

class VisaHistoryAdapter :
    PagingDataAdapter<VisaHistoryItem, VisaHistoryAdapter.HistoryViewHolder>(DIFF_UTIL) {

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): HistoryViewHolder {
        val layoutInflater = LayoutInflater.from(parent.context)
        val mBinding: ItemVisaHistoryBinding =
            DataBindingUtil.inflate(layoutInflater, R.layout.item_visa_history, parent, false)
        return HistoryViewHolder(mBinding)
    }

    override fun onBindViewHolder(holder: HistoryViewHolder, position: Int) {
        val item = getItem(position)
        item?.let { it ->
            if (it.paymentStatus == VisaPaymentStatus.UNPAID.value) {
                it.showingStatus = VisaPaymentStatus.UNPAID.value
                holder.mBinding.textViewStatus.setTextColor(
                    ContextCompat.getColor(
                        holder.mBinding.root.context,
                        R.color.error_color
                    )
                )
            } else if (it.paymentStatus == VisaPaymentStatus.PAID.value
                && it.bookingStatus == VisaBookingStatus.PENDING.value
            ) {
                it.showingStatus = VisaBookingStatus.PENDING.value
                holder.mBinding.textViewStatus.setTextColor(
                    ContextCompat.getColor(
                        holder.mBinding.root.context,
                        R.color.example_7_yellow
                    )
                )
            } else if (it.bookingStatus == VisaBookingStatus.CANCELLED.value) {
                it.showingStatus = VisaBookingStatus.CANCELLED.value
                holder.mBinding.textViewStatus.setTextColor(
                    ContextCompat.getColor(
                        holder.mBinding.root.context,
                        R.color.error_color
                    )
                )
            } else if (it.paymentStatus == VisaPaymentStatus.PAID.value && it.bookingStatus == VisaBookingStatus.PROCESSING.value) {
                it.showingStatus = VisaBookingStatus.PROCESSING.value
                holder.mBinding.textViewStatus.setTextColor(
                    ContextCompat.getColor(
                        holder.mBinding.root.context,
                        R.color.clear_blue
                    )
                )
            } else if (it.paymentStatus == VisaPaymentStatus.PAID.value && it.bookingStatus == VisaBookingStatus.CONFIRM.value) {
                it.showingStatus = "Successful"
                holder.mBinding.textViewStatus.setTextColor(
                    ContextCompat.getColor(
                        holder.mBinding.root.context,
                        R.color.mid_green
                    )
                )
            }
            holder.bind(it)
        }
    }

    fun getItemAtAdapterPosition(position: Int): VisaHistoryItem? {
        return getItem(position)
    }

    inner class HistoryViewHolder(val mBinding: ItemVisaHistoryBinding) :
        RecyclerView.ViewHolder(mBinding.root) {
        fun bind(item: VisaHistoryItem) {
            mBinding.historyItem = item
            mBinding.executePendingBindings()
        }
    }

    companion object {
        val DIFF_UTIL: DiffUtil.ItemCallback<VisaHistoryItem> =
            object : DiffUtil.ItemCallback<VisaHistoryItem>() {
                override fun areItemsTheSame(
                    oldItem: VisaHistoryItem,
                    newItem: VisaHistoryItem
                ): Boolean {
                    return oldItem.bookingCode == newItem.bookingCode
                }

                override fun areContentsTheSame(
                    oldItem: VisaHistoryItem,
                    newItem: VisaHistoryItem
                ): Boolean {
                    return oldItem == newItem
                }
            }
    }
}
