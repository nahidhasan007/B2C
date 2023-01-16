package net.sharetrip.hotel.booking.view.roomlist.adapter

import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.appcompat.widget.AppCompatImageView
import androidx.databinding.DataBindingUtil
import androidx.recyclerview.widget.RecyclerView
import com.bumptech.glide.Glide
import net.sharetrip.hotel.R
import net.sharetrip.hotel.booking.model.RoomDetails
import net.sharetrip.hotel.booking.view.roomlist.RoomItemNavigator
import net.sharetrip.hotel.databinding.ItemRoomLayoutBinding

class RoomAdapter(
    private val roomList: List<RoomDetails>,
    private val navigator: RoomItemNavigator,
    private val images: List<String>
) : RecyclerView.Adapter<RoomAdapter.RoomHolder>() {

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): RoomHolder {
        val viewHolder =
            DataBindingUtil.inflate<ItemRoomLayoutBinding>(
                LayoutInflater.from(parent.context),
                R.layout.item_room_layout, parent, false
            )
        return RoomHolder(viewHolder)
    }

    override fun getItemCount(): Int = roomList.size

    override fun onBindViewHolder(holder: RoomHolder, position: Int) {
        val roomItem = roomList[position]
        holder.binding.viewModel = roomItem

        holder.binding.roomDetails.setOnClickListener {
            navigator.openRoomDetails(images, roomItem)
        }

        roomItem.roomAmenities.forEachIndexed { index, roomAmenities ->
            if (index < 6) {
                val view = LayoutInflater.from(holder.binding.amenatiesLayout.context)
                    .inflate(R.layout.item_room_amenaties_icon, null, true)
                val amenitiesImage: AppCompatImageView = view.findViewById(R.id.ammenatiesItem)
                Glide.with(holder.binding.amenatiesLayout.context).load(roomAmenities.logoPng)
                    .into(amenitiesImage)
                holder.binding.amenatiesLayout.addView(view)
            }
        }
    }

    class RoomHolder(val binding: ItemRoomLayoutBinding) : RecyclerView.ViewHolder(binding.root)
}
