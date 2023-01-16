package net.sharetrip.bus.booking.view.seatselection

import android.graphics.Color
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import net.sharetrip.bus.databinding.ItemBusSeatBinding

class BusSeatSelectionAdapter(var viewModel: BusSeatSelectionViewModel, val size: Int = 50) :
    RecyclerView.Adapter<BusSeatSelectionAdapter.ViewHolder>() {

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
        val binding = ItemBusSeatBinding.inflate(LayoutInflater.from(parent.context), parent, false)
        return ViewHolder(binding)
    }

    override fun onBindViewHolder(holder: ViewHolder, position: Int) {
        if (size > 5) {
            if (viewModel.departureInfoMapped[position] != null) {
                holder.itemViewBinding.model = viewModel.departureInfoMapped[position]

                if (viewModel.getColorCode(
                        viewModel.departureInfoMapped[position]
                    ) != "none"
                ) {
                    holder.itemViewBinding.tintValue = Color.parseColor(
                        viewModel.getColorCode(
                            viewModel.departureInfoMapped[position]
                        )
                    )
                }
            } else {
                holder.itemViewBinding.parentMain.visibility = View.INVISIBLE
                holder.itemViewBinding.tintValue = Color.parseColor("#FCA213")
            }
        } else {
            holder.itemViewBinding.model = viewModel.seatModel[position]
            holder.itemViewBinding.tvSeatStatusValue.visibility = View.VISIBLE
            holder.itemViewBinding.tvSeatCountValue.visibility = View.VISIBLE
            holder.itemViewBinding.tintValue =
                Color.parseColor(viewModel.seatModel[position].colorCode)
        }
    }

    override fun getItemCount(): Int =
        if (size > 5) viewModel.seatCol * viewModel.departureInfo!!.seatRow else 5

    inner class ViewHolder(val itemViewBinding: ItemBusSeatBinding) :
        RecyclerView.ViewHolder(itemViewBinding.root)
}
