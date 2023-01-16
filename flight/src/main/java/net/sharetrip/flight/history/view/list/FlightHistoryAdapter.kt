package net.sharetrip.flight.history.view.list

import android.annotation.SuppressLint
import android.os.Build
import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.core.content.ContextCompat
import androidx.databinding.DataBindingUtil
import androidx.paging.PagingDataAdapter
import androidx.recyclerview.widget.DiffUtil
import androidx.recyclerview.widget.RecyclerView
import net.sharetrip.shared.utils.DateUtil
import net.sharetrip.flight.R
import net.sharetrip.flight.databinding.ItemOfFlightHistoryBinding
import net.sharetrip.flight.history.model.FlightHistoryResponse

class FlightHistoryAdapter :
    PagingDataAdapter<FlightHistoryResponse, FlightHistoryAdapter.HistoryViewHolder>(FlightHistoryUtil()) {
    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): HistoryViewHolder {
        val layoutInflater = LayoutInflater.from(parent.context)
        val mBinding = DataBindingUtil.inflate<ItemOfFlightHistoryBinding>(
            layoutInflater,
            R.layout.item_of_flight_history,
            parent,
            false
        )
        return HistoryViewHolder(mBinding)
    }

    @SuppressLint("SetTextI18n")
    override fun onBindViewHolder(holder: HistoryViewHolder, position: Int) {
        val historyResponse = getItem(position)
        holder.mBinding.item = historyResponse
        val flight = historyResponse!!.flight
        if (flight.size == 1) {
            val itemFlight = flight[0]
            val originCode = itemFlight.originCode
            val destinationCode = itemFlight.destinationCode
            holder.mBinding.flightCode.text = "$originCode - $destinationCode"
            val arrivalDateTime = itemFlight.arrivalDateTime
            val departureDateTime = itemFlight.departureDateTime
            val mBuilder = StringBuilder()
            mBuilder.append(DateUtil.parseDisplayDateFormatFromApiDateFormatData(departureDateTime!!.date))
            if (arrivalDateTime != null) {
                mBuilder.append(" - ")
                mBuilder.append(DateUtil.parseDisplayDateFormatFromApiDateFormatData(arrivalDateTime.date))
            }
            holder.mBinding.dateTextView.text = mBuilder.toString()
        } else if (flight.size > 1) {
            val itemStartFlight = flight[0]
            val itemEndFlight = flight[flight.size - 1]
            val originCode = itemStartFlight.originCode
            val destinationCode = itemEndFlight.originCode
            holder.mBinding.flightCode.text =
                "$originCode - $destinationCode - ${itemEndFlight.destinationCode}"
            val arrivalDateTime = itemStartFlight.departureDateTime
            val departureDateTime = itemEndFlight.departureDateTime
            val mBuilder = StringBuilder()
            mBuilder.append(DateUtil.parseDisplayDateFormatFromApiDateFormatData(arrivalDateTime!!.date))
            if (departureDateTime != null) {
                mBuilder.append(" - ")
                mBuilder.append(
                    DateUtil.parseDisplayDateFormatFromApiDateFormatData(
                        departureDateTime.date
                    )
                )
            }
            holder.mBinding.dateTextView.text = mBuilder.toString()
        }
        val status = historyResponse.bookingStatus
        val paymentStatus = historyResponse.paymentStatus
        val context = holder.mBinding.root.context
        if (status.contains("Cancelled") || paymentStatus.equals("Unpaid", ignoreCase = true)) {
            holder.mBinding.statusTextView.setTextColor(
                ContextCompat.getColor(
                    context,
                    R.color.error_color
                )
            )
        } else {
            holder.mBinding.statusTextView.setTextColor(
                ContextCompat.getColor(
                    context,
                    R.color.mid_green
                )
            )
        }
    }

    fun getItemAtAdapterPosition(position: Int): FlightHistoryResponse? {
        return getItem(position)
    }

    class HistoryViewHolder(val mBinding: ItemOfFlightHistoryBinding) :
        RecyclerView.ViewHolder(mBinding.root)

    class FlightHistoryUtil : DiffUtil.ItemCallback<FlightHistoryResponse>() {
        override fun areItemsTheSame(
            oldItem: FlightHistoryResponse,
            newItem: FlightHistoryResponse
        ): Boolean {
            return oldItem.sequenceCode == oldItem.sequenceCode
        }

        override fun areContentsTheSame(
            oldItem: FlightHistoryResponse,
            newItem: FlightHistoryResponse
        ): Boolean {
            return if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.KITKAT) {
                oldItem == newItem
            } else {
                oldItem.sequenceCode == newItem.sequenceCode
            }
        }
    }
}
