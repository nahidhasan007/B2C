package net.sharetrip.flight.booking.view.flightdetails.adapter

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.RadioButton
import androidx.databinding.DataBindingUtil
import androidx.recyclerview.widget.RecyclerView
import com.sharetrip.base.utils.ShareTripAppConstants.NO_BAGGAGE
import net.sharetrip.flight.R
import net.sharetrip.flight.booking.view.flightdetails.FlightDetailsViewModel
import net.sharetrip.flight.booking.model.luggage.RouteOptionsItem
import net.sharetrip.shared.databinding.ItemRouteBaggageHeaderBinding

class RouteBaggageAdapter(
    val viewModel: FlightDetailsViewModel,
    private val isBaggageOptional: Boolean
) : RecyclerView.Adapter<RouteBaggageAdapter.BaggageViewHolder>() {

    private val routeOptionsList: MutableList<RouteOptionsItem> = ArrayList()

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): BaggageViewHolder {
        val viewHolder = DataBindingUtil.inflate<ItemRouteBaggageHeaderBinding>(
            LayoutInflater.from(parent.context),
            R.layout.item_route_baggage_header, parent, false
        )
        return BaggageViewHolder(viewHolder)
    }

    override fun onBindViewHolder(holder: BaggageViewHolder, position: Int) {
        val routeOptionsItem = routeOptionsList[position]
        holder.binding.textViewBaggageTitle.text =
            routeOptionsItem.origin + " - " + routeOptionsItem.destination
        holder.binding.radioGroupBaggage.check(routeOptionsItem.selectedItem)

        routeOptionsItem.onlyAdultOptionList.forEach {
            val radioButton = RadioButton(holder.itemView.context)
            radioButton.tag = it.code
            radioButton.text =
                "Add " + it.details + " " + it.quantity + "(" + it.currency + " " + it.amount + " " + "Max " + it.quantity + "Bags)"
            holder.binding.radioGroupBaggage.addView(radioButton)
        }

        if (isBaggageOptional) {
            val radioButton = RadioButton(holder.itemView.context)
            radioButton.tag = NO_BAGGAGE
            radioButton.text = "Add No Baggage"
            holder.binding.radioGroupBaggage.addView(radioButton)
        }

        holder.binding.radioGroupBaggage.setOnCheckedChangeListener { radioGroup, checkedId ->
            routeOptionsItem.selectedItem = checkedId
            val radioButton = radioGroup.findViewById(checkedId) as RadioButton
            val tag = radioButton.tag.toString()
            if (tag.contentEquals(NO_BAGGAGE)) {
                routeOptionsItem.selectedCode = ""
            } else {
                routeOptionsItem.selectedCode = tag
            }

            viewModel.routeBaggage(routeOptionsList)
        }

        holder.binding.imageViewBaggageExpandable.setOnClickListener {
            routeOptionsItem.isExpanded = !routeOptionsItem.isExpanded
            if (routeOptionsItem.isExpanded) {
                holder.binding.imageViewBaggageExpandable.rotation = 0f
                holder.binding.radioGroupBaggage.visibility = View.GONE
            } else {
                holder.binding.imageViewBaggageExpandable.rotation = 180f
                holder.binding.radioGroupBaggage.visibility = View.VISIBLE
            }
        }
    }

    fun addRouteOptions(routeOptions: List<RouteOptionsItem>) {
        routeOptionsList.clear()
        routeOptionsList.addAll(routeOptions)
        notifyDataSetChanged()
    }

    override fun getItemCount() = routeOptionsList.size

    inner class BaggageViewHolder(val binding: ItemRouteBaggageHeaderBinding) :
        RecyclerView.ViewHolder(binding.root)

}