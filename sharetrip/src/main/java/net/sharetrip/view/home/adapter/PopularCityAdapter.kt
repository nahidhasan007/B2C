package net.sharetrip.view.home.adapter

import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.databinding.DataBindingUtil
import androidx.recyclerview.widget.RecyclerView
import com.bumptech.glide.Glide
import com.bumptech.glide.load.resource.bitmap.CenterCrop
import com.bumptech.glide.load.resource.bitmap.RoundedCorners
import com.bumptech.glide.request.RequestOptions
import net.sharetrip.R
import net.sharetrip.databinding.ItemTourCityBinding
import net.sharetrip.holiday.booking.model.HolidayCity
import java.util.*

class PopularCityAdapter : RecyclerView.Adapter<PopularCityAdapter.PopularCityViewHolder>() {
    private val cityList = ArrayList<HolidayCity>()

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): PopularCityViewHolder {
        val bindingView = DataBindingUtil.inflate<ItemTourCityBinding>(LayoutInflater.from(parent.context),
                R.layout.item_tour_city, parent, false)
        return PopularCityViewHolder(bindingView)
    }

    override fun getItemCount() = cityList.size

    override fun onBindViewHolder(holder: PopularCityViewHolder, position: Int) = holder.bind(cityList[position])

    fun update(cities: ArrayList<HolidayCity>) {
        cityList.clear()
        cityList.addAll(cities)
        notifyDataSetChanged()
    }

    inner class PopularCityViewHolder(private val cityBinding: ItemTourCityBinding) : RecyclerView.ViewHolder(cityBinding.root) {

        fun bind(city: HolidayCity) {
            cityBinding.textViewCityName.text = city.name

            val options = RequestOptions()
                    .centerCrop()
                    .transform(CenterCrop(), RoundedCorners(16))
                    .placeholder(R.drawable.ic_placeholder)
                    .error(R.drawable.ic_placeholder)

            Glide.with(cityBinding.imageViewCity.context)
                    .load(city.imageUrl)
                    .apply(options)
                    .into(cityBinding.imageViewCity)
        }
    }
}