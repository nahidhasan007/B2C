package net.sharetrip.profile.view.reward

import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.databinding.DataBindingUtil
import androidx.recyclerview.widget.RecyclerView
import net.sharetrip.profile.R
import net.sharetrip.profile.databinding.ItemTripCoinRewardWinnerBinding
import net.sharetrip.profile.model.TripCoinWinner
import java.util.*

class TripCoinRewardAdapter : RecyclerView.Adapter<TripCoinRewardAdapter.CoinWinnerViewHolder>() {
    private val tripCoinList = ArrayList<TripCoinWinner>()

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): CoinWinnerViewHolder {
        val viewHolder = DataBindingUtil.inflate<ItemTripCoinRewardWinnerBinding>(
            LayoutInflater.from(parent.context),
            R.layout.item_trip_coin_reward_winner, parent, false
        )
        return CoinWinnerViewHolder(viewHolder)
    }

    override fun onBindViewHolder(holder: CoinWinnerViewHolder, position: Int) {
        holder.tripCoinView.tripCoin = tripCoinList[position]
    }

    override fun getItemCount() = tripCoinList.size

    fun updateLeaderBoardList(dataList: List<TripCoinWinner>) {
        if (dataList != null) {
            this.tripCoinList.clear()
            this.tripCoinList.addAll(dataList)
            notifyDataSetChanged()
        }
    }

    class CoinWinnerViewHolder(val tripCoinView: ItemTripCoinRewardWinnerBinding) :
        RecyclerView.ViewHolder(tripCoinView.root)
}
