package net.sharetrip.hotel.booking.view.roomlist.adapter

import android.annotation.SuppressLint
import android.content.Context
import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.databinding.DataBindingUtil
import androidx.recyclerview.widget.RecyclerView
import androidx.recyclerview.widget.RecyclerView.Adapter
import net.sharetrip.hotel.R
import net.sharetrip.hotel.booking.model.Rooms
import net.sharetrip.hotel.booking.view.roomlist.RoomItemNavigator
import net.sharetrip.hotel.databinding.ItemRoomBundleLayoutBinding

class BundleAdapter(private val navigator: RoomItemNavigator, private var roomNumber: Int) :
    Adapter<BundleAdapter.RoomViewHolder>() {
    private val rooms = ArrayList<Rooms>()
    private lateinit var context: Context

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): RoomViewHolder {
        context = parent.context
        val bindingRoom =
            DataBindingUtil.inflate<ItemRoomBundleLayoutBinding>(
                LayoutInflater.from(context),
                R.layout.item_room_bundle_layout, parent, false
            )
        return RoomViewHolder(bindingRoom)
    }

    override fun getItemCount() = rooms.size

    override fun onBindViewHolder(holder: RoomViewHolder, position: Int) {
        val roomItem = rooms[position]
        if (roomItem.images.isNotEmpty()) {
            holder.roomView.imageUrl = roomItem.images[0]
        }

        holder.roomView.roomRecycler.apply {
            adapter = RoomAdapter(roomItem.rooms, navigator, roomItem.images)
        }
        holder.roomView.rateRecycler.apply {
            val sortedList = roomItem.rates.sortedWith(compareBy { it.price.total })
            adapter = RateAdapter(sortedList, navigator)
        }
    }

    @SuppressLint("NotifyDataSetChanged")
    fun updateRoomInfo(roomList: ArrayList<Rooms>, roomNumber: Int) {
        this.roomNumber = roomNumber
        rooms.clear()
        rooms.addAll(roomList)
        notifyDataSetChanged()
    }

    inner class RoomViewHolder(val roomView: ItemRoomBundleLayoutBinding) :
        RecyclerView.ViewHolder(roomView.root)
}
