package net.sharetrip.hotel.booking.view.hotellist

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.databinding.DataBindingUtil
import androidx.paging.PagingDataAdapter
import androidx.recyclerview.widget.RecyclerView
import com.bumptech.glide.Glide
import com.bumptech.glide.load.engine.DiskCacheStrategy
import com.bumptech.glide.load.resource.bitmap.CenterCrop
import com.bumptech.glide.load.resource.bitmap.RoundedCorners
import com.bumptech.glide.request.RequestOptions
import net.sharetrip.hotel.R
import net.sharetrip.hotel.booking.model.HotelInfo
import net.sharetrip.hotel.databinding.ItemHotelInfoBinding

class HotelListAdapter(private val roomCount: Int) :
    PagingDataAdapter<HotelInfo, HotelListAdapter.HotelInfoViewHolder>(HotelInfo.DIFF_CALLBACK) {

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): HotelInfoViewHolder {
        val hotelBinding =
            DataBindingUtil.inflate<ItemHotelInfoBinding>(
                LayoutInflater.from(parent.context),
                R.layout.item_hotel_info, parent, false
            )
        return HotelInfoViewHolder(hotelBinding)
    }

    override fun onBindViewHolder(holder: HotelInfoViewHolder, position: Int) {
        holder.bind(getItem(position))
    }

    inner class HotelInfoViewHolder(val bindingView: ItemHotelInfoBinding) :
        RecyclerView.ViewHolder(bindingView.root) {
        fun bind(hotelInfo: HotelInfo?) {
            bindingView.hotelInfo = hotelInfo
            bindingView.roomCount = roomCount
            bindingView.ratingBar.removeAllViews()
            val inflater = LayoutInflater.from(bindingView.ratingBar.context)

            for (i in 1..hotelInfo!!.starRating.toInt()) {
                val item: View = inflater.inflate(R.layout.star_layout, null, true)
                bindingView.ratingBar.addView(item)
            }

            val option = RequestOptions()
                .centerCrop()
                .placeholder(R.drawable.ic_placeholder)
                .error(R.drawable.ic_placeholder)
                .diskCacheStrategy(DiskCacheStrategy.ALL)
                .transform(CenterCrop(), RoundedCorners(6))

            Glide.with(bindingView.hotelThumbImageView.context)
                .load(hotelInfo.thumbnail)
                .apply(option)
                .into(bindingView.hotelThumbImageView)
        }
    }
}
