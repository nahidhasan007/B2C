package net.sharetrip.bus.booking.view.locationpoints

import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import net.sharetrip.bus.booking.model.BoardingPoints
import net.sharetrip.bus.databinding.ItemBusSelectLocationBinding

class PointsAdapter(
    val values: List<BoardingPoints>,
    val viewModel: SelectLocationPointsViewModel
) : RecyclerView.Adapter<PointsAdapter.ViewHolder>() {
    private var selected = -1

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
        val inflater = LayoutInflater.from(parent.context)
        val binding = ItemBusSelectLocationBinding.inflate(inflater, parent, false)
        return ViewHolder(binding)
    }

    override fun onBindViewHolder(holder: ViewHolder, position: Int) {
        holder.itemViewBinding.model = values[position]
        holder.itemViewBinding.mainParent.setOnClickListener {
            values[position].isSelected = true

            if (selected >= 0 && selected != position) {
                values[selected].isSelected = false
                notifyItemChanged(selected)
            }

            selected = position
            viewModel.onPointSelected(position)
            notifyItemChanged(position)
        }
    }

    override fun getItemCount(): Int = values.size

    inner class ViewHolder(val itemViewBinding: ItemBusSelectLocationBinding) :
        RecyclerView.ViewHolder(itemViewBinding.root)
}
