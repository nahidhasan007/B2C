package net.sharetrip.flight.booking.view.travellerdetails.adapter

import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.databinding.DataBindingUtil
import androidx.recyclerview.widget.RecyclerView
import net.sharetrip.shared.utils.Strings
import net.sharetrip.shared.view.adapter.BaseRecyclerAdapter
import net.sharetrip.flight.R
import net.sharetrip.flight.booking.model.ItemTraveler
import net.sharetrip.flight.booking.view.travellerdetails.adapter.TravelerAddAdapter.ItemAddViewHolder
import net.sharetrip.flight.databinding.ItemAddTravelerOfFlightBinding

class TravelerAddAdapter : BaseRecyclerAdapter<ItemTraveler, ItemAddViewHolder>() {
    override fun onCreateViewHolder(mPrent: ViewGroup, viewType: Int): ItemAddViewHolder {
        val mContext = mPrent.context
        val inflater = LayoutInflater.from(mContext)
        val mView = DataBindingUtil.inflate<ItemAddTravelerOfFlightBinding>(inflater, R.layout.item_add_traveler_of_flight, mPrent, false)
        return ItemAddViewHolder(mView)
    }

    override fun onBindViewHolder(holder: ItemAddViewHolder, mPosition: Int) {
        val mItemTraveler = getItem(mPosition)
        val mTitle = if (Strings.isNull(mItemTraveler.surName)) mItemTraveler.passengerTypeTitle else "${mItemTraveler.givenName} ${mItemTraveler.surName}"
        holder.mIteView.personButton.text = mTitle
        holder.mIteView.personButton.isChecked = mItemTraveler.isValidData
    }

    inner class ItemAddViewHolder(val mIteView: ItemAddTravelerOfFlightBinding) : RecyclerView.ViewHolder(mIteView.root)
}