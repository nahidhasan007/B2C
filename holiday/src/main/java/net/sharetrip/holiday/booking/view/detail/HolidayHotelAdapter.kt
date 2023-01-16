package net.sharetrip.holiday.booking.view.detail

import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.databinding.DataBindingUtil
import androidx.recyclerview.widget.RecyclerView
import com.example.holiday.R
import com.example.holiday.databinding.ItemHolidayHotelLayoutBinding
import net.sharetrip.holiday.booking.model.Hotel
import java.util.*

class HolidayHotelAdapter : RecyclerView.Adapter<HolidayHotelAdapter.HolidayHotelHolder>() {
    private val hotelList = ArrayList<Hotel>()

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): HolidayHotelHolder {
        val binding = DataBindingUtil.inflate<ItemHolidayHotelLayoutBinding>(LayoutInflater.from(parent.context),
                R.layout.item_holiday_hotel_layout, parent, false)
        return HolidayHotelHolder(binding)
    }

    override fun getItemCount() = hotelList.size

    override fun onBindViewHolder(holder: HolidayHotelHolder, position: Int) {
        holder.bind(hotelList[position])
    }

    fun update(hotel: List<Hotel>) {
        hotelList.clear()
        hotelList.addAll(hotel)
        notifyDataSetChanged()
    }

    inner class HolidayHotelHolder(private val binding: ItemHolidayHotelLayoutBinding)
        : RecyclerView.ViewHolder(binding.root) {

        fun bind(item: Hotel) {
            val hotelName = "${item.hotelName} (${item.cityName})"
            binding.textViewHotelName.text = hotelName
        }
    }
}
