package net.sharetrip.holiday.booking.view.main

import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.databinding.DataBindingUtil
import androidx.recyclerview.widget.RecyclerView
import com.example.holiday.R
import com.example.holiday.databinding.ItemHolidayDestinationBinding
import net.sharetrip.holiday.booking.model.Destination

class DestinationAdapter(private val viewModel: HolidayHeaderViewModel) :
    RecyclerView.Adapter<DestinationAdapter.DestinationViewHolder>() {
    private val destinationsList = ArrayList<Destination>()

    init {
        destinationsList.add(Destination(cityCode = "1", cityName = "Destination City"))
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): DestinationViewHolder {
        val bindingView = DataBindingUtil.inflate<ItemHolidayDestinationBinding>(
            LayoutInflater.from(parent.context), R.layout.item_holiday_destination, parent, false
        )
        return DestinationViewHolder(bindingView)
    }

    override fun getItemCount() = destinationsList.size

    override fun onBindViewHolder(holder: DestinationViewHolder, holderPosition: Int) {

        holder.bindingView.layoutDestinationCity.setOnClickListener {
            viewModel.onDestinationFieldClicked(holderPosition)
        }

        holder.bindingView.imageViewClose.setOnClickListener {
            viewModel.onRemovedDestinationItem(holderPosition)
        }

        val item = destinationsList[holderPosition]
        item.position = holderPosition
        holder.bindingView.destination = item
    }

    fun update(destinations: ArrayList<Destination>) {
        destinationsList.clear()
        destinationsList.addAll(destinations)
        notifyDataSetChanged()
    }

    inner class DestinationViewHolder(val bindingView: ItemHolidayDestinationBinding) :
        RecyclerView.ViewHolder(bindingView.root)
}
