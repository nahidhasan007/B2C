package net.sharetrip.flight.booking.view.passenger.baggageinsurance

import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import net.sharetrip.flight.booking.model.BaggageInsurance
import net.sharetrip.flight.databinding.ItemBaggageInsuranceProviderBinding

class BaggageInsuranceProviderAdapter(
    private val baggageInsuranceList: ArrayList<BaggageInsurance>,
    private val listener: ItemClickListener
) : RecyclerView.Adapter<BaggageInsuranceProviderAdapter.BaggageInsuranceServiceHolder>() {

    override fun onCreateViewHolder(
        parent: ViewGroup,
        viewType: Int
    ): BaggageInsuranceServiceHolder {
        val viewHolder = ItemBaggageInsuranceProviderBinding.inflate(
            LayoutInflater.from(parent.context),
            parent,
            false
        )
        return BaggageInsuranceServiceHolder(viewHolder)
    }

    override fun onBindViewHolder(holder: BaggageInsuranceServiceHolder, position: Int) {
        holder.binding.baggageInsuranceItem = baggageInsuranceList[position]

        holder.binding.root.setOnClickListener {
            listener.onItemClick(baggageInsuranceList[position].code)
        }
    }

    override fun getItemCount(): Int {
        return baggageInsuranceList.size
    }

    inner class BaggageInsuranceServiceHolder(val binding: ItemBaggageInsuranceProviderBinding) :
        RecyclerView.ViewHolder(binding.root)

    interface ItemClickListener {
        fun onItemClick(code: String)
    }
}
