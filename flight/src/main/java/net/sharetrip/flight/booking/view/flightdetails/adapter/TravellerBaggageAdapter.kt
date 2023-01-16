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
import net.sharetrip.flight.booking.model.luggage.TravellerBaggage
import net.sharetrip.shared.databinding.ItemTravellerBaggageHeaderBinding

class TravellerBaggageAdapter(
    val viewModel: FlightDetailsViewModel,
    val isBaggageOptional: Boolean
) : RecyclerView.Adapter<TravellerBaggageAdapter.BaggageViewHolder>() {

    private val travellerBaggageList: MutableList<TravellerBaggage> = ArrayList()

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): BaggageViewHolder {
        val viewHolder = DataBindingUtil.inflate<ItemTravellerBaggageHeaderBinding>(
            LayoutInflater.from(parent.context),
            R.layout.item_traveller_baggage_header, parent, false
        )
        return BaggageViewHolder(viewHolder)
    }

    override fun onBindViewHolder(holder: BaggageViewHolder, position: Int) {
        val travellerBaggage = travellerBaggageList[position]
        holder.binding.textViewBaggageTitle.text = travellerBaggage.title

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
        }

        if (isBaggageOptional) {
            val radioButton = RadioButton(holder.itemView.context)
            radioButton.tag = NO_BAGGAGE
            radioButton.text = "Add No Baggage"
            holder.binding.radioGroupBaggage.addView(radioButton)
        }

        holder.binding.radioGroupBaggage.setOnCheckedChangeListener { radioGroup, checkedId ->
            travellerBaggage.selectedItem = checkedId
            val radioButton = radioGroup.findViewById(checkedId) as RadioButton
            val tag = radioButton.tag.toString()
            if (tag.contentEquals(NO_BAGGAGE)) {
                travellerBaggage.selectedCode = ""
            } else {
                travellerBaggage.selectedCode = tag
            }

            viewModel.travellerBaggage(travellerBaggageList)
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

    fun addTravellerList(routeOptions: List<TravellerBaggage>) {
        travellerBaggageList.clear()
        travellerBaggageList.addAll(routeOptions)
        notifyDataSetChanged()
    }

    override fun getItemCount() = travellerBaggageList.size

    inner class BaggageViewHolder(val binding: ItemTravellerBaggageHeaderBinding) :
        RecyclerView.ViewHolder(binding.root)

}