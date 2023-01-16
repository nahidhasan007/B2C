package net.sharetrip.visa.booking.view.checkout

import android.annotation.SuppressLint
import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.databinding.DataBindingUtil
import androidx.recyclerview.widget.RecyclerView
import net.sharetrip.visa.R
import net.sharetrip.visa.booking.model.VisaItemTraveler
import net.sharetrip.visa.databinding.ItemVisaCheckoutBinding

class TravellerAdapter : RecyclerView.Adapter<TravellerAdapter.TravellerHolder>() {
    private val travellerList = ArrayList<VisaItemTraveler>()

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): TravellerHolder {
        val countryView = DataBindingUtil.inflate<ItemVisaCheckoutBinding>(
            LayoutInflater.from(parent.context),
            R.layout.item_visa_checkout,
            parent,
            false
        )
        return TravellerHolder(countryView)
    }

    @SuppressLint("SetTextI18n")
    override fun onBindViewHolder(holder: TravellerHolder, position: Int) {
        val traveler = travellerList[position]
        if (traveler.givenName!!.isEmpty()) {
            holder.bindingView.tvTravellerName.text = "Traveler " + (position + 1)
        } else
            holder.bindingView.tvTravellerName.text = traveler.givenName
    }

    override fun getItemCount() = travellerList.size

    fun updateTravelerList(list: List<VisaItemTraveler>?) {
        if (list != null) {
            this.travellerList.clear()
            this.travellerList.addAll(list)
            notifyDataSetChanged()
        }
    }

    inner class TravellerHolder(val bindingView: ItemVisaCheckoutBinding) :
        RecyclerView.ViewHolder(bindingView.root)
}
