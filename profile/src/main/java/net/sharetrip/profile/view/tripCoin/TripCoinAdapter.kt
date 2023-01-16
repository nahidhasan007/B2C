package net.sharetrip.profile.view.tripCoin

import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.databinding.DataBindingUtil
import androidx.recyclerview.widget.RecyclerView
import net.sharetrip.profile.R
import net.sharetrip.profile.databinding.ItemTripCoinProfileModBinding
import net.sharetrip.profile.model.TripCoinItem
import java.util.*

class TripCoinAdapter: RecyclerView.Adapter<TripCoinAdapter.TripCoinViewHolder>() {
    private val tripCoinItems = ArrayList<TripCoinItem>()

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): TripCoinViewHolder {
        val tripCoinsView = DataBindingUtil.
        inflate<ItemTripCoinProfileModBinding>(
                LayoutInflater.from(parent.context),
                R.layout.item_trip_coin_profile_mod,
                parent,
                false)
        return TripCoinViewHolder(tripCoinsView)
    }

    override fun getItemCount(): Int = tripCoinItems.size

    override fun onBindViewHolder(holder: TripCoinViewHolder, position: Int) {
        holder.tripCoinView.tripCoinItem = tripCoinItems[position]
    }

    fun updateTripCoinList(coinList: ArrayList<TripCoinItem>) {
        tripCoinItems.addAll(coinList)
        notifyDataSetChanged()
    }

    inner class TripCoinViewHolder(val tripCoinView: ItemTripCoinProfileModBinding) : RecyclerView.ViewHolder(tripCoinView.root)
}
