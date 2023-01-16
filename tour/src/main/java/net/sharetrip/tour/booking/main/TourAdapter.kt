package net.sharetrip.tour.booking.main

import android.content.Context
import android.view.LayoutInflater
import android.view.ViewGroup
import android.widget.Toast
import androidx.databinding.DataBindingUtil
import androidx.paging.PagingDataAdapter
import androidx.recyclerview.widget.RecyclerView
import com.bumptech.glide.Glide
import com.bumptech.glide.request.RequestOptions
import com.sharetrip.base.network.NetworkUtil
import net.sharetrip.shared.view.adapter.ItemClickSupport
import net.sharetrip.tour.R
import net.sharetrip.tour.databinding.ItemTourHeaderBinding
import net.sharetrip.tour.databinding.ItemTourNewBinding
import net.sharetrip.tour.model.TourItem
import java.text.NumberFormat
import java.util.*

class TourAdapter(private val headerViewModel: TourMainViewModel) :
    PagingDataAdapter<TourItem, RecyclerView.ViewHolder>(
        TourItem.DIFF_CALLBACK
    ) {
    private lateinit var context: Context

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): RecyclerView.ViewHolder {
        context = parent.context
        if (viewType == VIEW_TYPE_ITEM) {
            val binding = DataBindingUtil.inflate<ItemTourNewBinding>(
                LayoutInflater.from(parent.context),
                R.layout.item_tour_new, parent, false
            )
            return TourViewHolder(binding)
        }

        val binding = DataBindingUtil.inflate<ItemTourHeaderBinding>(
            LayoutInflater.from(parent.context),
            R.layout.item_tour_header, parent, false
        )
        return TourHeaderViewHolder(binding)
    }

    override fun onBindViewHolder(holder: RecyclerView.ViewHolder, position: Int) {
        if (holder is TourViewHolder) {
            holder.bind(getItem(position - 1)!!)
        } else if (holder is TourHeaderViewHolder) {
            holder.showData(headerViewModel)
        }
    }

    override fun getItemViewType(position: Int): Int {
        if (position == 0)
            return VIEW_TYPE_HEADER
        return VIEW_TYPE_ITEM
    }

    inner class TourViewHolder(private val tourBinding: ItemTourNewBinding) :
        RecyclerView.ViewHolder(tourBinding.root) {

        fun bind(item: TourItem) {
            tourBinding.textViewTime.text = item.duration + " " + item.durationType
            tourBinding.textViewTripCoin.text =
                NumberFormat.getNumberInstance(Locale.US).format(item.points?.earning)
            tourBinding.textViewShareCoin.text =
                NumberFormat.getNumberInstance(Locale.US).format(item.points?.shared)
            tourBinding.textViewPrice.text =
                NumberFormat.getNumberInstance(Locale.US).format(item.lowestPrice)
            tourBinding.textViewDescription.text = item.title
            tourBinding.textViewLocation.text = item.cityName + ", " + item.countryName
            tourBinding.textViewTime.text = item.duration + " Hours"

            if (item.images?.size!! > 0)
                Glide.with(tourBinding.imageViewTrip.context)
                    .load(item.images?.get(0))
                    .apply(RequestOptions().centerCrop())
                    .into(tourBinding.imageViewTrip)
        }
    }

    inner class TourHeaderViewHolder(private val tourHeader: ItemTourHeaderBinding) :
        RecyclerView.ViewHolder(tourHeader.root) {

        private val cityAdapter = TourCityAdapter()

        fun showData(viewModel: TourMainViewModel) {
            tourHeader.viewModel = viewModel
            tourHeader.listPopularCities.adapter = cityAdapter
            viewModel.liveCities.observeForever { tourCities -> cityAdapter.update(tourCities) }

            ItemClickSupport.addTo(tourHeader.listPopularCities)
                .setOnItemClickListener { recyclerView, position, v ->
                    if (NetworkUtil.hasNetwork(context))
                        viewModel.navigateToTourList(position)
                    else
                        Toast.makeText(context, "No internet", Toast.LENGTH_LONG).show()
                }
        }
    }

    companion object {
        private const val VIEW_TYPE_HEADER = 0
        private const val VIEW_TYPE_ITEM = 1
    }
}
