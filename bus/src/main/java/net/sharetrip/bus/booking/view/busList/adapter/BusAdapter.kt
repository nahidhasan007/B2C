package net.sharetrip.bus.booking.view.busList.adapter

import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.databinding.DataBindingUtil
import androidx.recyclerview.widget.RecyclerView
import net.sharetrip.shared.view.adapter.BaseRecyclerAdapter
import net.sharetrip.bus.R
import net.sharetrip.bus.booking.model.Departure
import net.sharetrip.bus.databinding.ItemBusBinding

class BusAdapter(var tripValue: Int = 0) :
    BaseRecyclerAdapter<Departure, BusAdapter.BusViewHolder>() {

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): BusViewHolder {
        val inflater = LayoutInflater.from(parent.context)
        val cityBinding = DataBindingUtil.inflate<ItemBusBinding>(
            inflater,
            R.layout.item_bus, parent, false
        )
        return BusViewHolder(cityBinding)
    }

    override fun onBindViewHolder(holder: BusViewHolder, position: Int) {
        holder.binding.departure = getItem(position)
        holder.binding.tripCoinValue = tripValue
        holder.bindingListener()
    }

    inner class BusViewHolder(val binding: ItemBusBinding) : RecyclerView.ViewHolder(binding.root) {
        fun bindingListener() {

        }
    }

    interface BusItemClickListener {
        fun onItemClick()
    }
}
