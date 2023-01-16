package net.sharetrip.hotel.booking.view.main

import android.annotation.SuppressLint
import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.databinding.DataBindingUtil
import androidx.recyclerview.widget.RecyclerView
import net.sharetrip.hotel.R
import net.sharetrip.hotel.booking.model.ReviewCity
import net.sharetrip.hotel.databinding.ItemTripWishListBinding
import net.sharetrip.shared.utils.loadImageWithRoundCorner

class WishListTripAdapter(private val callback: WishListTripNavigator) :
    RecyclerView.Adapter<WishListTripAdapter.WishTripViewHolder>() {
    private val cityList = ArrayList<ReviewCity>()

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): WishTripViewHolder {
        val binding = DataBindingUtil.inflate<ItemTripWishListBinding>(
            LayoutInflater.from(parent.context),
            R.layout.item_trip_wish_list, parent, false
        )
        return WishTripViewHolder(binding)
    }

    override fun getItemCount() = cityList.size

    override fun onBindViewHolder(holder: WishTripViewHolder, position: Int) {
        holder.bindData(position, callback)
    }

    @SuppressLint("NotifyDataSetChanged")
    fun update(cities: List<ReviewCity>) {
        cityList.clear()
        cityList.addAll(cities)
        notifyDataSetChanged()
    }

    inner class WishTripViewHolder(private val tripBinding: ItemTripWishListBinding) :
        RecyclerView.ViewHolder(tripBinding.root) {

        @SuppressLint("SetTextI18n")
        fun bindData(position: Int, callback: WishListTripNavigator) {
            tripBinding.textViewWishCityName.text =
                "Do you want to go to " + cityList[position].city.name + " ?"
            tripBinding.imageViewCity.loadImageWithRoundCorner(cityList[position].cdn, 24)

            tripBinding.btnHaveBeenThere.setOnClickListener {
                callback.selectHaveBeenThere(cityList[position].city.cityCode)
                callback.navigateToNext(position)
            }

            tripBinding.btnWantToGo.setOnClickListener {
                callback.selectWannaGoThere(cityList[position].city.cityCode)
                callback.navigateToNext(position)
            }
        }
    }
}
