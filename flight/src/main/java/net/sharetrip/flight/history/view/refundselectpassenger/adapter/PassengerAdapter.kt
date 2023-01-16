package net.sharetrip.flight.history.view.refundselectpassenger.adapter

import android.annotation.SuppressLint
import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.appcompat.widget.SwitchCompat
import androidx.databinding.DataBindingUtil
import androidx.recyclerview.widget.RecyclerView
import net.sharetrip.flight.R
import net.sharetrip.flight.databinding.ItemRefundVoidPassengerSelectionBinding
import net.sharetrip.flight.history.model.Traveller

class PassengerAdapter(
    private var dataList: List<Traveller> = listOf(),
    private val listener: (Int, Boolean) -> Unit
) : RecyclerView.Adapter<PassengerAdapter.PassengerViewHolder>() {

    private var isAllSelected = false

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): PassengerViewHolder {
        val layoutInflater = LayoutInflater.from(parent.context)
        val binding = DataBindingUtil.inflate<ItemRefundVoidPassengerSelectionBinding>(
            layoutInflater,
            R.layout.item_refund_void_passenger_selection,
            parent,
            false
        )
        return PassengerViewHolder(binding)
    }

    override fun onBindViewHolder(holder: PassengerViewHolder, position: Int) {
        holder.itemViewBinding.passengerNameTextView.text =
            dataList[position].givenName + " " + dataList[position].surName

        holder.itemViewBinding.selectSwitch.isChecked = isAllSelected

        holder.itemViewBinding.selectSwitch.setOnClickListener {
            if ((it as SwitchCompat).isChecked)
                listener.invoke(position, true)
            else
                listener.invoke(position, false)
        }

        if (position == dataList.size - 1)
            isAllSelected = false
    }

    override fun getItemCount(): Int = dataList.size

    @SuppressLint("NotifyDataSetChanged")
    fun resetItems(updateList: List<Traveller>) {
        dataList = updateList
        notifyDataSetChanged()
    }

    @SuppressLint("NotifyDataSetChanged")
    fun selectAll() {
        isAllSelected = true
        notifyDataSetChanged()
    }

    inner class PassengerViewHolder(val itemViewBinding: ItemRefundVoidPassengerSelectionBinding) :
        RecyclerView.ViewHolder(itemViewBinding.root)
}
