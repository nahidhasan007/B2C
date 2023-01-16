package net.sharetrip.flight.booking.view.passenger.travelinsurance

import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import net.sharetrip.flight.booking.model.TravelInsuranceItem
import net.sharetrip.flight.databinding.ItemTravelInsuranceServiceFlightBinding

class InsuranceProviderAdapter(
    private val travelInsuranceList: ArrayList<TravelInsuranceItem>,
    private val listener: ItemClickListener
) : RecyclerView.Adapter<InsuranceProviderAdapter.TravelInsuranceServiceHolder>() {

    override fun onCreateViewHolder(
        parent: ViewGroup,
        viewType: Int
    ): TravelInsuranceServiceHolder {
        val viewHolder = ItemTravelInsuranceServiceFlightBinding.inflate(
            LayoutInflater.from(parent.context),
            parent,
            false
        )
        return TravelInsuranceServiceHolder(viewHolder)
    }

    override fun onBindViewHolder(holder: TravelInsuranceServiceHolder, position: Int) {
        holder.binding.travelInsuranceItem = travelInsuranceList[position]

        holder.binding.root.setOnClickListener {
            listener.onItemClick(travelInsuranceList[position].code)
        }
    }

    override fun getItemCount(): Int {
        return travelInsuranceList.size
    }

    inner class TravelInsuranceServiceHolder(val binding: ItemTravelInsuranceServiceFlightBinding) :
        RecyclerView.ViewHolder(binding.root)

    interface ItemClickListener {
        fun onItemClick(code: String)
    }
}
