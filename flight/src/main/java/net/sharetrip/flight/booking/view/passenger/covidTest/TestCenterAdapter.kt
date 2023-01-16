package net.sharetrip.flight.booking.view.passenger.covidTest

import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import net.sharetrip.flight.booking.model.CovidAddOnResponseItem
import net.sharetrip.flight.databinding.ItemCovidServiceFlightBinding
import java.util.*

class TestCenterAdapter(private val covidServiceList: ArrayList<CovidAddOnResponseItem>, private val listener: ItemClickListener) : RecyclerView.Adapter<TestCenterAdapter.CovidServiceHolder>() {

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): CovidServiceHolder {
        val viewHolder = ItemCovidServiceFlightBinding.inflate(LayoutInflater.from(parent.context), parent, false)
        return CovidServiceHolder(viewHolder)
    }

    override fun onBindViewHolder(holder: CovidServiceHolder, position: Int) {
        holder.binding.covidAddOns = covidServiceList[position]

        holder.binding.root.setOnClickListener {
            listener.onItemClick(covidServiceList[position].code)
        }
    }

    override fun getItemCount(): Int {
        return covidServiceList.size
    }

    inner class CovidServiceHolder(val binding: ItemCovidServiceFlightBinding) : RecyclerView.ViewHolder(binding.root)

    interface ItemClickListener {
        fun onItemClick(code: String)
    }
}