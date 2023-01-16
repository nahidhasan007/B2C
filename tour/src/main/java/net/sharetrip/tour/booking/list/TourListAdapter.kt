package net.sharetrip.tour.booking.list

import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.databinding.DataBindingUtil
import androidx.paging.PagingDataAdapter
import androidx.recyclerview.widget.RecyclerView
import com.bumptech.glide.Glide
import com.bumptech.glide.request.RequestOptions
import net.sharetrip.tour.R
import net.sharetrip.tour.databinding.ItemTourNewBinding
import net.sharetrip.tour.model.TourItem
import java.text.NumberFormat
import java.util.*

class TourListAdapter :
    PagingDataAdapter<TourItem, TourListAdapter.TourItemViewHolder>(TourItem.DIFF_CALLBACK) {

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): TourItemViewHolder {
        val binding = DataBindingUtil.inflate<ItemTourNewBinding>(
            LayoutInflater.from(parent.context),
            R.layout.item_tour_new, parent, false
        )
        return TourItemViewHolder(binding)
    }

    override fun onBindViewHolder(holder: TourItemViewHolder, position: Int) {
        holder.bind(getItem(position))
    }

    inner class TourItemViewHolder(private val tourBinding: ItemTourNewBinding) :
        RecyclerView.ViewHolder(tourBinding.root) {

        fun bind(item: TourItem?) {
            tourBinding.textViewTime.text = item!!.duration + " " + item.durationType
            tourBinding.textViewTripCoin.text = NumberFormat.getNumberInstance(Locale.US).format(
                item.points.earning
            )
            tourBinding.textViewShareCoin.text = NumberFormat.getNumberInstance(Locale.US).format(
                item.points.shared
            )
            tourBinding.textViewPrice.text =
                NumberFormat.getNumberInstance(Locale.US).format(item.lowestPrice)
            tourBinding.textViewDescription.text = item.title
            tourBinding.textViewTime.text = item.duration + " Hours"

            if (item.images.isNotEmpty())
                Glide.with(tourBinding.imageViewTrip.context)
                    .load(item.images[0])
                    .apply(RequestOptions().centerCrop())
                    .into(tourBinding.imageViewTrip)
        }
    }
}
