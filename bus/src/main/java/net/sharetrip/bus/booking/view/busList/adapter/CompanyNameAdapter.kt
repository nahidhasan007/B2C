package net.sharetrip.bus.booking.view.busList.adapter

import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.databinding.DataBindingUtil
import androidx.recyclerview.widget.RecyclerView
import net.sharetrip.shared.view.adapter.BaseRecyclerAdapter
import net.sharetrip.bus.R
import net.sharetrip.bus.booking.model.CompanyName
import net.sharetrip.bus.databinding.ItemBusProvidersBinding
import net.sharetrip.bus.booking.view.busList.BusListViewModel

class CompanyNameAdapter(val viewModel: BusListViewModel) :
    BaseRecyclerAdapter<CompanyName, CompanyNameAdapter.BusNameViewHolder>() {

    var selectedPosition = -1

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): BusNameViewHolder {
        val inflater = LayoutInflater.from(parent.context)
        val cityBinding = DataBindingUtil.inflate<ItemBusProvidersBinding>(
            inflater,
            R.layout.item_bus_providers, parent, false
        )
        return BusNameViewHolder(cityBinding)
    }

    override fun onBindViewHolder(holder: BusNameViewHolder, position: Int) {
        holder.binding.companyName = getItem(position)
        holder.binding.isSelected = position == selectedPosition

        holder.binding.root.setOnClickListener{
            viewModel.filterByCompanyName(getItem(position))
            val previousSelectedPosition = selectedPosition
            selectedPosition = position
            notifyItemChanged(previousSelectedPosition)
            notifyItemChanged(position)
        }
    }

    inner class BusNameViewHolder(val binding: ItemBusProvidersBinding) : RecyclerView.ViewHolder(binding.root)
}
