package net.sharetrip.hotel.booking.view.search

import android.annotation.SuppressLint
import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.databinding.DataBindingUtil
import androidx.recyclerview.widget.RecyclerView
import androidx.recyclerview.widget.RecyclerView.Adapter
import net.sharetrip.hotel.R
import net.sharetrip.hotel.booking.model.HotelProperty
import net.sharetrip.hotel.databinding.ItemHotelPropertyBinding

class PropertyAdapter : Adapter<PropertyAdapter.PropertyViewHolder>() {
    private val propertyList = ArrayList<HotelProperty>()

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): PropertyViewHolder {
        val bindingView = DataBindingUtil.inflate<ItemHotelPropertyBinding>(
            LayoutInflater.from(parent.context), R.layout.item_hotel_property,
            parent, false
        )
        return PropertyViewHolder(bindingView)
    }

    override fun getItemCount() = propertyList.size

    override fun onBindViewHolder(holder: PropertyViewHolder, position: Int) {
        holder.bindingView.property = propertyList[position]
    }

    @SuppressLint("NotifyDataSetChanged")
    fun updateData(properties: ArrayList<HotelProperty>) {
        propertyList.clear()
        propertyList.addAll(properties)
        notifyDataSetChanged()
    }

    inner class PropertyViewHolder(val bindingView: ItemHotelPropertyBinding) :
        RecyclerView.ViewHolder(bindingView.root)
}
