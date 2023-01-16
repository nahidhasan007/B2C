package net.sharetrip.visa.booking.view.countrysearch

import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.databinding.DataBindingUtil
import androidx.recyclerview.widget.RecyclerView
import net.sharetrip.visa.R
import net.sharetrip.visa.booking.model.VisaCountry
import net.sharetrip.visa.databinding.ItemSearchVisaCountryBinding

class VisaCountrySearchAdapter :
    RecyclerView.Adapter<VisaCountrySearchAdapter.CountryViewHolder>() {
    private val cityList = ArrayList<VisaCountry>()

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): CountryViewHolder {
        val bindingItem = DataBindingUtil.inflate<ItemSearchVisaCountryBinding>(
            LayoutInflater.from(parent.context),
            R.layout.item_search_visa_country, parent, false
        )
        return CountryViewHolder(bindingItem)
    }

    override fun getItemCount() = cityList.size

    override fun onBindViewHolder(holder: CountryViewHolder, position: Int) {
        holder.bindingView.country = cityList[position]
    }

    fun updateData(cities: ArrayList<VisaCountry>) {
        cityList.clear()
        cityList.addAll(cities)
        notifyDataSetChanged()
    }

    inner class CountryViewHolder(val bindingView: ItemSearchVisaCountryBinding) :
        RecyclerView.ViewHolder(bindingView.root)
}
