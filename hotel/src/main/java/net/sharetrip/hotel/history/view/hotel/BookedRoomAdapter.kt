package net.sharetrip.hotel.history.view.hotel

import android.annotation.SuppressLint
import android.content.Context
import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.databinding.DataBindingUtil
import androidx.recyclerview.widget.RecyclerView
import com.google.gson.Gson
import net.sharetrip.shared.utils.loadImage
import net.sharetrip.hotel.R
import net.sharetrip.hotel.databinding.ItemHotelHistoryRoomSummaryBinding
import net.sharetrip.hotel.history.model.BookedRoom
import net.sharetrip.hotel.history.model.Points

class BookedRoomAdapter(var roomInfoList: List<BookedRoom>, private val thumbnail: String) :
    RecyclerView.Adapter<BookedRoomAdapter.MyViewHolder>() {
    lateinit var context: Context

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): MyViewHolder {
        context = parent.context
        val inflater = LayoutInflater.from(context)
        val binding = DataBindingUtil.inflate<ItemHotelHistoryRoomSummaryBinding>(inflater,
            R.layout.item_hotel_history_room_summary, parent, false)
        return MyViewHolder(binding)
    }

    @SuppressLint("SetTextI18n")
    override fun onBindViewHolder(holder: MyViewHolder, position: Int) {
        val (_, price, adults, _, child, _) = roomInfoList[position]

        holder.mBinding.textViewRoomPrice.text = "BDT $price"
        holder.mBinding.header.text = context.getString(R.string.room) + (position + 1)
        holder.mBinding.textMember.text = "Adult $adults Child $child"
        holder.mBinding.textViewBedName.text = context.getString(R.string.bed_in_dorms)
        holder.mBinding.imageView.loadImage(thumbnail)

        val point = Gson().fromJson(roomInfoList[position].points, Points::class.java)
        holder.mBinding.textViewCoin.text = point.earning.toString()
    }

    override fun getItemCount(): Int = roomInfoList.size

    class MyViewHolder(var mBinding: ItemHotelHistoryRoomSummaryBinding) :
        RecyclerView.ViewHolder(mBinding.root)
}
