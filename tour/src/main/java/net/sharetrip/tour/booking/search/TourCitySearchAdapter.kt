package net.sharetrip.tour.booking.search

import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.databinding.DataBindingUtil
import androidx.recyclerview.widget.RecyclerView
import net.sharetrip.tour.R
import net.sharetrip.tour.databinding.ItemSearchTourCityBinding
import net.sharetrip.tour.model.TourPopCity

class TourCitySearchAdapter : RecyclerView.Adapter<TourCitySearchAdapter.CityInfoViewHolder>() {

    private val cityList = ArrayList<TourPopCity>()

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): CityInfoViewHolder {
        val bindingItem = DataBindingUtil.inflate<ItemSearchTourCityBinding>(
            LayoutInflater.from(parent.context),
            R.layout.item_search_tour_city, parent, false
        )
        return CityInfoViewHolder(bindingItem)
    }

    override fun getItemCount() = cityList.size

    override fun onBindViewHolder(holder: CityInfoViewHolder, position: Int) {
        holder.bindingView.city = cityList[position]
    }

    fun updateData(cities: ArrayList<TourPopCity>) {
        cityList.clear()
        cityList.addAll(cities)
        notifyDataSetChanged()
    }

    inner class CityInfoViewHolder(val bindingView: ItemSearchTourCityBinding) :
        RecyclerView.ViewHolder(bindingView.root)
}
