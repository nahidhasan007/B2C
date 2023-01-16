package net.sharetrip.profile.view.leaderboard

import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.databinding.DataBindingUtil
import androidx.recyclerview.widget.RecyclerView
import com.bumptech.glide.Glide
import com.bumptech.glide.load.engine.DiskCacheStrategy
import com.bumptech.glide.request.RequestOptions
import net.sharetrip.profile.R
import net.sharetrip.profile.databinding.ItemTripCoinWinnerBinding
import net.sharetrip.profile.model.TripCoinWinner
import java.util.*

class TripCoinAdapter : RecyclerView.Adapter<TripCoinAdapter.CoinWinnerViewHolder>() {
    private val winnerList = ArrayList<TripCoinWinner>()

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): CoinWinnerViewHolder {
        val viewHolder = DataBindingUtil.inflate<ItemTripCoinWinnerBinding>(LayoutInflater.from(parent.context),
                R.layout.item_trip_coin_winner, parent, false)
        return CoinWinnerViewHolder(viewHolder)
    }

    override fun onBindViewHolder(holder: CoinWinnerViewHolder, position: Int) {
        holder.coinWinnerView.coinWinner = winnerList[position]
        holder.coinWinnerView.countView.text = (position + 1).toString()
        val imageUrl = winnerList[position].avatar

        val requestOptions = RequestOptions().circleCrop()
                .diskCacheStrategy(DiskCacheStrategy.NONE) // because file name is always same
                .skipMemoryCache(true)
                .placeholder(R.drawable.ic_avatar_ash_40dp)
                .error(R.drawable.ic_avatar_ash_40dp)

        Glide.with(holder.coinWinnerView.iconUserImage.context)
                .load(imageUrl)
                .apply(requestOptions)
                .into(holder.coinWinnerView.iconUserImage)
    }

    override fun getItemCount() = winnerList.size

    fun updateLeaderBoardList(dataList: List<TripCoinWinner>) {
        if (dataList != null) {
            this.winnerList.clear()
            this.winnerList.addAll(dataList)
            notifyDataSetChanged()
        }
    }

    class CoinWinnerViewHolder(val coinWinnerView: ItemTripCoinWinnerBinding) : RecyclerView.ViewHolder(coinWinnerView.root)
}
