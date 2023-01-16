package net.sharetrip.holiday.history.view.detail

import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.databinding.DataBindingUtil
import androidx.recyclerview.widget.RecyclerView
import com.example.holiday.R
import com.example.holiday.databinding.ItemCityAndHotelBinding
import net.sharetrip.holiday.history.model.HotelInfo

class CustomAdapter(private var infoList: List<HotelInfo>) : RecyclerView.Adapter<CustomAdapter.MyViewHolder>() {

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): MyViewHolder {
        val bindView = DataBindingUtil.inflate<ItemCityAndHotelBinding>(
            LayoutInflater.from(parent.context),
            R.layout.item_city_and_hotel,
            parent, false)
        return MyViewHolder(bindView)
    }

    override fun getItemCount() = infoList.size

    override fun onBindViewHolder(holder: MyViewHolder, position: Int) {
        val info = infoList[position]
        holder.binding.tvCity.text = info.cityName
        holder.binding.tvHotel.text = info.hotelName
    }

    class MyViewHolder(val binding: ItemCityAndHotelBinding) : RecyclerView.ViewHolder(binding.root)
}
