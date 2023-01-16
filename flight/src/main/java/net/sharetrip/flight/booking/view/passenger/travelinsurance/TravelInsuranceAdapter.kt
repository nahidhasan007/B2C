package net.sharetrip.flight.booking.view.passenger.travelinsurance

import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import net.sharetrip.flight.booking.model.TravelInsuranceOption
import net.sharetrip.flight.databinding.ItemTravelInsuranceItemFlightBinding

class TravelInsuranceAdapter(
    private val travelInsuranceOption: ArrayList<TravelInsuranceOption>,
    private var selectedCode: String,
    val listener: TravelInsuranceAddOnsListener
) : RecyclerView.Adapter<TravelInsuranceAdapter.TravelInsuranceViewHolder>() {
    var selectedPosition = -1
    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): TravelInsuranceViewHolder {
        val viewHolder =
            ItemTravelInsuranceItemFlightBinding.inflate(
                LayoutInflater.from(parent.context),
                parent,
                false
            )
        return TravelInsuranceViewHolder(viewHolder)
    }

    override fun getItemCount() = travelInsuranceOption.size

    inner class TravelInsuranceViewHolder(val binding: ItemTravelInsuranceItemFlightBinding) :
        RecyclerView.ViewHolder(binding.root)

    interface TravelInsuranceAddOnsListener {
        fun onClickItem(option: TravelInsuranceOption)
    }

    override fun onBindViewHolder(holder: TravelInsuranceViewHolder, position: Int) {

        if (selectedCode == travelInsuranceOption[position].code) {
            selectedPosition = position
        }

        if (position == selectedPosition) {
            listener.onClickItem(travelInsuranceOption[position])
        }
        travelInsuranceOption[position].isSelected = position == selectedPosition

        holder.binding.travelInsuranceOption = travelInsuranceOption[position]
        holder.binding.isActive = position == selectedPosition

        holder.binding.cardView.setOnClickListener {
            val previousSelectedPosition = selectedPosition
            selectedPosition = position
            selectedCode = travelInsuranceOption[position].code
            notifyItemChanged(previousSelectedPosition)
            notifyItemChanged(position)
        }
    }
}