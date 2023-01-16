package net.sharetrip.tour.booking.main

import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.databinding.DataBindingUtil
import androidx.recyclerview.widget.RecyclerView
import net.sharetrip.shared.utils.loadImageWithRoundCorner
import net.sharetrip.tour.R
import net.sharetrip.tour.databinding.ItemTourCityNewBinding
import net.sharetrip.tour.model.TourPopCity

class TourCityAdapter : RecyclerView.Adapter<TourCityAdapter.CityViewHolder>() {

    private val cityList = ArrayList<TourPopCity>()

    init {
        cityList.add(TourPopCity())
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): CityViewHolder {
        val binding = DataBindingUtil.inflate<ItemTourCityNewBinding>(
            LayoutInflater.from(parent.context),
            R.layout.item_tour_city_new, parent, false
        )
        return CityViewHolder(binding)
    }

    override fun onBindViewHolder(holder: CityViewHolder, position: Int) {
        holder.bind(cityList[position])
    }

    override fun getItemCount() = cityList.size

    fun update(cities: List<TourPopCity>) {
        cityList.clear()
        cityList.addAll(cities)
        notifyDataSetChanged()
    }

    inner class CityViewHolder(private val cityBinding: ItemTourCityNewBinding) :
        RecyclerView.ViewHolder(cityBinding.root) {

        fun bind(city: TourPopCity) {
            cityBinding.textViewCityName.text = city.name
            cityBinding.imageViewCity.loadImageWithRoundCorner(city.image, 16)
        }
    }
}
