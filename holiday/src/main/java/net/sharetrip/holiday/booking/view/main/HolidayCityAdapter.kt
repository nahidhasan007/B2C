package net.sharetrip.holiday.booking.view.main

import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.databinding.DataBindingUtil
import androidx.recyclerview.widget.RecyclerView
import com.example.holiday.R
import com.example.holiday.databinding.ItemTourCityOfHolidayBinding
import net.sharetrip.shared.utils.loadImageWithRoundCorner
import net.sharetrip.holiday.booking.model.HolidayCity
import java.util.*

class HolidayCityAdapter : RecyclerView.Adapter<HolidayCityAdapter.CityViewHolder>() {

    private val cityList = ArrayList<HolidayCity>()

    init {
        cityList.add(HolidayCity(name = "", code = "", countryCode = "", countryName = "", imageUrl = ""))
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): CityViewHolder {
        val binding = DataBindingUtil.inflate<ItemTourCityOfHolidayBinding>(LayoutInflater.from(parent.context),
                R.layout.item_tour_city_of_holiday, parent, false)
        return CityViewHolder(binding)
    }

    override fun onBindViewHolder(holder: CityViewHolder, position: Int) {
        holder.bind(cityList[position])
    }

    override fun getItemCount() = cityList.size


    fun update(cities: List<HolidayCity>) {
        cityList.clear()
        cityList.addAll(cities)
        notifyDataSetChanged()
    }

    inner class CityViewHolder(private val cityBinding: ItemTourCityOfHolidayBinding) : RecyclerView.ViewHolder(cityBinding.root) {

        fun bind(city: HolidayCity) {
            cityBinding.textViewCityName.text = city.name
            cityBinding.imageViewCity.loadImageWithRoundCorner(city.imageUrl,16)
        }
    }
}
