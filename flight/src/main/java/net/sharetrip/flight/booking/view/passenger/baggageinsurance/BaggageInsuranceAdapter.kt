package net.sharetrip.flight.booking.view.passenger.baggageinsurance

import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import net.sharetrip.flight.booking.model.BaggageInsuranceOption
import net.sharetrip.flight.databinding.ItemBaggageInsuranceBinding

class BaggageInsuranceAdapter(
    private val baggageInsuranceOption: ArrayList<BaggageInsuranceOption>,
    private var selectedCode: String,
    val listener: BaggageInsuranceListener
) : RecyclerView.Adapter<BaggageInsuranceAdapter.BaggageInsuranceViewHolder>() {
    var selectedPosition = -1
    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): BaggageInsuranceViewHolder {
        val viewHolder =
            ItemBaggageInsuranceBinding.inflate(
                LayoutInflater.from(parent.context),
                parent,
                false
            )
        return BaggageInsuranceViewHolder(viewHolder)
    }

    override fun getItemCount() = baggageInsuranceOption.size

    inner class BaggageInsuranceViewHolder(val binding: ItemBaggageInsuranceBinding) :
        RecyclerView.ViewHolder(binding.root)

    interface BaggageInsuranceListener {
        fun onClickItem(option: BaggageInsuranceOption)
    }

    override fun onBindViewHolder(holder: BaggageInsuranceViewHolder, position: Int) {

        if (selectedCode == baggageInsuranceOption[position].optionCode) {
            selectedPosition = position
        }

        if (position == selectedPosition) {
            listener.onClickItem(baggageInsuranceOption[position])
        }
        baggageInsuranceOption[position].isSelected = position == selectedPosition

        holder.binding.baggageInsuranceOption = baggageInsuranceOption[position]
        holder.binding.isActive = position == selectedPosition

        holder.binding.cardView.setOnClickListener {
            val previousSelectedPosition = selectedPosition
            selectedPosition = position
            selectedCode = baggageInsuranceOption[position].optionCode
            notifyItemChanged(previousSelectedPosition)
            notifyItemChanged(position)
        }
    }
}