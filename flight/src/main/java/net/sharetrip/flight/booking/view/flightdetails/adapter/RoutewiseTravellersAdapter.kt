package net.sharetrip.flight.booking.view.flightdetails.adapter

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.RadioButton
import androidx.databinding.DataBindingUtil
import androidx.recyclerview.widget.RecyclerView
import com.sharetrip.base.utils.ShareTripAppConstants
import io.github.luizgrp.sectionedrecyclerviewadapter.Section
import io.github.luizgrp.sectionedrecyclerviewadapter.SectionParameters
import net.sharetrip.flight.R
import net.sharetrip.flight.booking.view.flightdetails.FlightDetailsViewModel
import net.sharetrip.flight.booking.model.luggage.RouteOptionsItem
import net.sharetrip.flight.booking.model.luggage.TravellerBaggage
import net.sharetrip.shared.databinding.ItemRouteBaggageHeaderBinding
import net.sharetrip.shared.databinding.ItemTravellerBaggageHeaderBinding

class RoutewiseTravellersAdapter(
    private val viewModel: FlightDetailsViewModel,
    private val routeOptionsItem: RouteOptionsItem,
    private var travellersBaggageList: MutableList<TravellerBaggage> = ArrayList(),
    private val isBaggageOptional: Boolean, private val routePosition: Int
) : Section(
    SectionParameters.builder()
        .itemResourceId(R.layout.item_traveller_baggage_header)
        .headerResourceId(R.layout.item_route_baggage_header)
        .build()
) {

    override fun getContentItemsTotal() = travellersBaggageList.size

    override fun getItemViewHolder(parent: View): TravellerViewHolder {
        val viewHolder = DataBindingUtil.inflate<ItemTravellerBaggageHeaderBinding>(
            LayoutInflater.from(parent.context),
            R.layout.item_traveller_baggage_header, parent as ViewGroup, false
        )

        return TravellerViewHolder(viewHolder)
    }

    override fun onBindItemViewHolder(holder: RecyclerView.ViewHolder, position: Int) {
        val itemHolder = holder as TravellerViewHolder
        val travellerBaggage = travellersBaggageList[position]

        itemHolder.binding.textViewBaggageTitle.text = travellerBaggage.title
        holder.binding.radioGroupBaggage.check(travellerBaggage.selectedItem)

        holder.binding.imageViewBaggageExpandable
//            .setTint(
//            ContextCompat.getColor(
//                holder.itemView.context,
//                R.color.blue_blue
//            )
//        )

        travellerBaggage.optionList.forEach {
            val radioButton = RadioButton(holder.itemView.context)
            radioButton.tag = it.code
            radioButton.text =
                "Add " + it.details + " " + it.quantity + "(" + it.currency + " " + it.amount + " " + "Max " + it.quantity + "Bags)"
            holder.binding.radioGroupBaggage.addView(radioButton)

            if (it.amount == 0.0) {
                radioButton.isChecked = true
                travellerBaggage.selectedCode =  it.code
                viewModel.routeAndTravellerBaggage(routePosition, travellersBaggageList)
            }
        }

        if (isBaggageOptional) {
            val radioButton = RadioButton(holder.itemView.context)
            radioButton.tag = ShareTripAppConstants.NO_BAGGAGE
            radioButton.text = "Add No Baggage"
            holder.binding.radioGroupBaggage.addView(radioButton)
        }

        holder.binding.radioGroupBaggage.setOnCheckedChangeListener { radioGroup, checkedId ->
            travellerBaggage.selectedItem = checkedId
            val radioButton = radioGroup.findViewById(checkedId) as RadioButton
            val tag = radioButton.tag.toString()
            if (tag.contentEquals(ShareTripAppConstants.NO_BAGGAGE)) {
                travellerBaggage.selectedCode = ""
            } else {
                travellerBaggage.selectedCode = tag
            }
            viewModel.routeAndTravellerBaggage(routePosition, travellersBaggageList)
        }

        holder.binding.imageViewBaggageExpandable.setOnClickListener {
            travellerBaggage.isExpanded = !travellerBaggage.isExpanded
            if (travellerBaggage.isExpanded) {
                holder.binding.imageViewBaggageExpandable.setImageResource(R.drawable.ic_plus_mono_24dp)
                holder.binding.radioGroupBaggage.visibility = View.GONE
            } else {
                holder.binding.imageViewBaggageExpandable.setImageResource(R.drawable.ic_minus_mono_24dp)
                holder.binding.radioGroupBaggage.visibility = View.VISIBLE
            }
        }
    }

    override fun getHeaderViewHolder(parent: View): RouteBaggageViewHolder {
        val viewHolder = DataBindingUtil.inflate<ItemRouteBaggageHeaderBinding>(
            LayoutInflater.from(parent.context),
            R.layout.item_route_baggage_header, parent as ViewGroup, false
        )

        return RouteBaggageViewHolder(viewHolder)
    }

    override fun onBindHeaderViewHolder(holder: RecyclerView.ViewHolder) {
        super.onBindHeaderViewHolder(holder)
        val headerViewHolder = holder as RouteBaggageViewHolder
        headerViewHolder.binding.textViewBaggageTitle.text =
            routeOptionsItem.origin + " - " + routeOptionsItem.destination
        headerViewHolder.binding.imageViewBaggageExpandable.visibility = View.GONE
    }

    inner class RouteBaggageViewHolder(val binding: ItemRouteBaggageHeaderBinding) :
        RecyclerView.ViewHolder(binding.root)

    inner class TravellerViewHolder(val binding: ItemTravellerBaggageHeaderBinding) :
        RecyclerView.ViewHolder(binding.root)

}