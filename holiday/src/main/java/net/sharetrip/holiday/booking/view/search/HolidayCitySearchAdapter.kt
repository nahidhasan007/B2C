package net.sharetrip.holiday.booking.view.search

import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.databinding.DataBindingUtil
import androidx.recyclerview.widget.RecyclerView
import com.example.holiday.R
import com.example.holiday.databinding.ItemSearchHolidayCityBinding
import net.sharetrip.holiday.booking.model.HolidayCity
import java.util.*

class HolidayCitySearchAdapter : RecyclerView.Adapter<HolidayCitySearchAdapter.CityInfoViewHolder>() {
    private val cityList = ArrayList<HolidayCity>()

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): CityInfoViewHolder {
        val bindingItem = DataBindingUtil.inflate<ItemSearchHolidayCityBinding>(LayoutInflater.from(parent.context),
                R.layout.item_search_holiday_city, parent, false)
        return CityInfoViewHolder(bindingItem)
    }

    override fun getItemCount() = cityList.size

    override fun onBindViewHolder(holder: CityInfoViewHolder, position: Int) {
       holder.bindingView.city = cityList[position]
    }

    fun updateData(cities: ArrayList<HolidayCity>) {
        cityList.clear()
        cityList.addAll(cities)
        notifyDataSetChanged()
    }

    inner class CityInfoViewHolder(val bindingView: ItemSearchHolidayCityBinding) : RecyclerView.ViewHolder(bindingView.root)
}
