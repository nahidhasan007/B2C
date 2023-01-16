package net.sharetrip.tour.history.list

import android.graphics.Color
import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.databinding.DataBindingUtil
import androidx.paging.PagingDataAdapter
import androidx.recyclerview.widget.RecyclerView
import net.sharetrip.shared.utils.DateUtil
import net.sharetrip.tour.R
import net.sharetrip.tour.databinding.ItemTourHistoryBinding
import net.sharetrip.tour.model.TourHistoryItem
import java.text.ParseException

class TourHistoryAdapter :
    PagingDataAdapter<TourHistoryItem, TourHistoryAdapter.HistoryViewHolder>(TourHistoryItem.DIFF_UTIL) {

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): HistoryViewHolder {
        val layoutInflater = LayoutInflater.from(parent.context)
        val mBinding = DataBindingUtil.inflate<ItemTourHistoryBinding>(
            layoutInflater,
            R.layout.item_tour_history,
            parent,
            false
        )
        return HistoryViewHolder(mBinding)
    }

    override fun onBindViewHolder(holder: HistoryViewHolder, position: Int) {
        val item = getItem(position)
        holder.bind(item!!)
    }

    fun getItemAtAdapterPosition(position: Int): TourHistoryItem? {
        return getItem(position)
    }

    class HistoryViewHolder(val mBinding: ItemTourHistoryBinding) :
        RecyclerView.ViewHolder(mBinding.root) {

        fun bind(item: TourHistoryItem) {
            if (item.paymentStatus.equals("Cancelled") || item.paymentStatus.equals(
                    "UnPaid", true
                )
            )
                mBinding.textViewStatus.setTextColor(Color.RED)
            else
                mBinding.textViewStatus.setTextColor(Color.parseColor("#43a046"))

            mBinding.data = item
            try {
                val date = DateUtil.parseDisplayDateFormatFromApiDateFormat(item.tourDate)
                val parts = date.split(" ".toRegex(), 2).toTypedArray()
                mBinding.textViewTourDate.text = parts[0]
                mBinding.textViewTourDateInMonthYear.text = parts[1]
            } catch (e: ParseException) {
                e.printStackTrace()
            } catch (e: NumberFormatException) {
                e.printStackTrace()
            }
        }
    }
}
