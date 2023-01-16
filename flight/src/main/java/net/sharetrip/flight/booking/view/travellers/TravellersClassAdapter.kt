package net.sharetrip.flight.booking.view.travellers

import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.core.content.ContextCompat
import androidx.databinding.DataBindingUtil
import androidx.recyclerview.widget.RecyclerView
import net.sharetrip.flight.R

import net.sharetrip.flight.databinding.ItemTravelClassBinding

class TravellersClassAdapter(private val classList: List<String>, val viewModel: TravellerNumberViewModel) :
    RecyclerView.Adapter<TravellersClassAdapter.TravellersClassHolder>() {

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): TravellersClassHolder {
        val viewHolder = DataBindingUtil.inflate<ItemTravelClassBinding>(
            LayoutInflater.from(parent.context),
            R.layout.item_travel_class, parent, false
        )
        return TravellersClassHolder(viewHolder)
    }

    override fun getItemCount() = classList.size

    override fun onBindViewHolder(holder: TravellersClassHolder, position: Int) {
        val className: String = classList[position]

        holder.binding.className = className
        holder.binding.buttonTravelClass.checked(className == viewModel.tripClassType)
        if (className == viewModel.tripClassType){
            holder.binding.buttonTravelClass.setTextColor(ContextCompat.getColor(holder.itemView.context, R.color.clear_blue))
        } else {
            holder.binding.buttonTravelClass.setTextColor(ContextCompat.getColor(holder.itemView.context, R.color.greyish_brown))
        }
        holder.binding.buttonTravelClass.setOnClickListener() {
            viewModel.tripClassType = className
            notifyDataSetChanged()
        }
    }

    class TravellersClassHolder(val binding: ItemTravelClassBinding) :
        RecyclerView.ViewHolder(binding.root)
}