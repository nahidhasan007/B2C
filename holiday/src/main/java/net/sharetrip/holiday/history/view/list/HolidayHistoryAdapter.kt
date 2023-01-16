package net.sharetrip.holiday.history.view.list

import android.os.Build
import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.core.content.ContextCompat
import androidx.databinding.DataBindingUtil
import androidx.paging.PagingDataAdapter
import androidx.recyclerview.widget.DiffUtil
import androidx.recyclerview.widget.RecyclerView
import com.example.holiday.R
import com.example.holiday.databinding.ItemHolidayHistoryBinding
import net.sharetrip.shared.utils.DateUtil
import net.sharetrip.holiday.history.model.HolidayHistoryItem
import java.text.NumberFormat
import java.util.*

class HolidayHistoryAdapter :
    PagingDataAdapter<HolidayHistoryItem, HolidayHistoryAdapter.HistoryViewHolder>(DIFF_UTIL) {
    private val Booking_id = "Booking ID: "

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): HistoryViewHolder {
        val layoutInflater = LayoutInflater.from(parent.context)
        val mBinding: ItemHolidayHistoryBinding =
            DataBindingUtil.inflate(layoutInflater, R.layout.item_holiday_history, parent, false)
        return HistoryViewHolder(mBinding)
    }

    override fun onBindViewHolder(holder: HistoryViewHolder, position: Int) {
        val item = getItem(position)
        try {
            holder.mBinding.textViewBookingId.text = Booking_id + item!!.bookingCode
            holder.mBinding.textViewTransferName.text = item.bookedPackage.title
            holder.mBinding.textViewTourDate.text = DateUtil.getNumberOfDay(item.packageDate)
            holder.mBinding.textViewTourDateInMonthYear.text =
                DateUtil.getMonthYearTrimmed(item.packageDate!!)
            holder.mBinding.textViewCurrencySym.text = item.currencyCode
            holder.mBinding.textViewCurrency.text =
                NumberFormat.getNumberInstance(Locale.US).format(item.totalAmount.toLong())
            holder.mBinding.textViewGuest.text = item.travellers
            holder.mBinding.textViewTravelTime.text = item.arrivalTime
            holder.mBinding.textViewStatus.text = item.bookingStatus
            holder.mBinding.textViewStatus.setTextColor(
                ContextCompat.getColor(
                    holder.mBinding.textViewStatus.context,
                    R.color.mid_green
                )
            )

            when (item.bookingStatus) {
                "Cancelled" -> {
                    holder.mBinding.textViewStatus.setTextColor(
                        ContextCompat.getColor(
                            holder.mBinding.textViewStatus.context,
                            R.color.error_color
                        )
                    )
                }
                "Processing" -> {
                    holder.mBinding.textViewStatus.text = item.paymentStatus
                    if (item.paymentStatus?.equals("UnPaid")!!) {
                        holder.mBinding.textViewStatus.setTextColor(
                            ContextCompat.getColor(
                                holder.mBinding.textViewStatus.context,
                                R.color.error_color
                            )
                        )
                    }
                }
            }
        } catch (e: Exception) {
            e.printStackTrace()
        }
    }

    fun getItemAtAdapterPosition(position: Int): HolidayHistoryItem? {
        return getItem(position)
    }

    inner class HistoryViewHolder(val mBinding: ItemHolidayHistoryBinding) :
        RecyclerView.ViewHolder(mBinding.root)

    companion object {
        val DIFF_UTIL: DiffUtil.ItemCallback<HolidayHistoryItem> =
            object : DiffUtil.ItemCallback<HolidayHistoryItem>() {
                override fun areItemsTheSame(
                    oldItem: HolidayHistoryItem,
                    newItem: HolidayHistoryItem
                ): Boolean {
                    return oldItem.bookingCode === oldItem.bookingCode
                }

                override fun areContentsTheSame(
                    oldItem: HolidayHistoryItem,
                    newItem: HolidayHistoryItem
                ): Boolean {
                    return if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.KITKAT) {
                        oldItem == newItem
                    } else {
                        oldItem.bookingCode == newItem.bookingCode
                    }
                }
            }
    }
}
