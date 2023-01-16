package net.sharetrip.hotel.booking.view.travelleroom

import android.annotation.SuppressLint
import android.content.Context
import android.view.LayoutInflater
import android.view.ViewGroup
import android.widget.ArrayAdapter
import android.widget.Toast
import androidx.databinding.DataBindingUtil
import androidx.recyclerview.widget.RecyclerView
import net.sharetrip.hotel.R
import net.sharetrip.hotel.booking.model.TravellerRoomInfo
import net.sharetrip.hotel.databinding.ItemTravellerRoomBinding

class TravellerRoomAdapter : RecyclerView.Adapter<TravellerRoomAdapter.TravellerRoomViewHolder>(),
    RoomRemoveListener {
    private var rooms = ArrayList<TravellerRoomInfo>()
    private lateinit var context: Context

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): TravellerRoomViewHolder {
        val roomBinding = DataBindingUtil.inflate<ItemTravellerRoomBinding>(
            LayoutInflater.from(parent.context),
            R.layout.item_traveller_room, parent, false
        )
        context = parent.context

        return TravellerRoomViewHolder(roomBinding)
    }

    override fun onBindViewHolder(holder: TravellerRoomViewHolder, position: Int) {
        val viewModel = RoomViewModel(position, rooms[position], this)
        holder.bindingView.viewModel = viewModel

        val childAgeArrayList = context.resources.getStringArray(R.array.hotel_children_age)
        val childAgeAdapter =
            ArrayAdapter(context, android.R.layout.simple_dropdown_item_1line, childAgeArrayList)

        holder.bindingView.firstChildrenAgeSpinner.setAdapter(childAgeAdapter)
        holder.bindingView.secondChildrenAgeSpinner.setAdapter(childAgeAdapter)
    }

    override fun getItemCount() = rooms.size

    @SuppressLint("NotifyDataSetChanged")
    fun updateData(roomList: ArrayList<TravellerRoomInfo>) {
        this.rooms = roomList
        notifyDataSetChanged()
    }

    @SuppressLint("NotifyDataSetChanged")
    override fun removeRoom(index: Int) {
        if (rooms.size == 1) {
            Toast.makeText(
                context,
                context.getString(R.string.one_room_warning),
                Toast.LENGTH_SHORT
            ).show()
        } else {
            rooms.removeAt(index)
            notifyDataSetChanged()
        }
    }

    inner class TravellerRoomViewHolder(val bindingView: ItemTravellerRoomBinding) :
        RecyclerView.ViewHolder(bindingView.root)
}
