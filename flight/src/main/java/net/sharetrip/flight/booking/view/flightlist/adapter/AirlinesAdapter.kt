package net.sharetrip.flight.booking.view.flightlist.adapter

import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.databinding.DataBindingUtil
import androidx.recyclerview.widget.RecyclerView
import net.sharetrip.flight.R
import net.sharetrip.flight.booking.model.filter.Airline
import net.sharetrip.flight.databinding.ItemAirlinesLayoutBinding

class AirlinesAdapter(
    private val airlineList: List<Airline>,
    private val listener: AirlinesListener
) : RecyclerView.Adapter<AirlinesAdapter.AirlinesHolder>() {
    var selectedCode = ""

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): AirlinesHolder {
        val viewHolder = DataBindingUtil.inflate<ItemAirlinesLayoutBinding>(
            LayoutInflater.from(parent.context),
            R.layout.item_airlines_layout, parent, false
        )
        return AirlinesHolder(viewHolder)
    }

    override fun getItemCount(): Int = airlineList.size

    override fun onBindViewHolder(holder: AirlinesHolder, position: Int) {
        val airline = airlineList[position]
        holder.binding.data = airline
        holder.binding.selectedCode = selectedCode
        holder.binding.root.setOnClickListener {
            listener.onCodeSelect(airline.code)
            selectedCode = airline.code
            notifyDataSetChanged()
        }
    }

    class AirlinesHolder(val binding: ItemAirlinesLayoutBinding) :
        RecyclerView.ViewHolder(binding.root)
}

interface AirlinesListener {
    fun onCodeSelect(code: String)
}
