package net.sharetrip.profile.view.leaderboard

import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.databinding.DataBindingUtil
import androidx.recyclerview.widget.RecyclerView
import com.bumptech.glide.Glide
import com.bumptech.glide.load.engine.DiskCacheStrategy
import com.bumptech.glide.request.RequestOptions
import net.sharetrip.profile.R
import net.sharetrip.profile.databinding.ItemRewardWinnerNewBinding
import net.sharetrip.profile.model.RewardWinner
import java.util.*

class SpecialRewardAdapter : RecyclerView.Adapter<SpecialRewardAdapter.SpecialRewardViewHolder>() {
    private val winnerList = ArrayList<RewardWinner>()

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): SpecialRewardViewHolder {
        val viewHolder = DataBindingUtil.inflate<ItemRewardWinnerNewBinding>(
            LayoutInflater.from(parent.context),
            R.layout.item_reward_winner_new, parent, false
        )
        return SpecialRewardViewHolder(viewHolder)
    }

    override fun getItemCount() = winnerList.size

    override fun onBindViewHolder(holder: SpecialRewardViewHolder, position: Int) {
        val winnerInfo = winnerList[position]
        holder.specialWinnerView.rewardWinner = winnerInfo
        val imageUrl = winnerInfo.avatar
        val requestOptions = RequestOptions().circleCrop()
            .diskCacheStrategy(DiskCacheStrategy.NONE) // because file name is always same
            .skipMemoryCache(true)
            .placeholder(R.drawable.ic_avatar_ash_40dp)
            .error(R.drawable.ic_avatar_ash_40dp)

        Glide.with(holder.specialWinnerView.iconWinnerImage.context)
            .load(imageUrl)
            .apply(requestOptions)
            .into(holder.specialWinnerView.iconWinnerImage)

        when (winnerInfo.type) {
            "Hotel" -> holder.specialWinnerView.iconType
                .setImageResource(R.drawable.ic_hotel_56dp)
            "Flight" -> holder.specialWinnerView.iconType
                .setImageResource(R.drawable.ic_flight_56dp)
            "Holiday" -> holder.specialWinnerView.iconType
                .setImageResource(R.drawable.ic_holiday_light_24dp)
            "Tour" -> holder.specialWinnerView.iconType
                .setImageResource(R.drawable.ic_tours_56dp)
            "Transport" -> holder.specialWinnerView.iconType
                .setImageResource(R.drawable.ic_transport_light_24dp)
        }
    }

    fun updateRewardWinnerList(dataList: List<RewardWinner>?) {
        if (dataList != null) {
            this.winnerList.clear()
            this.winnerList.addAll(dataList)
            notifyDataSetChanged()
        }
    }

    class SpecialRewardViewHolder(val specialWinnerView: ItemRewardWinnerNewBinding) :
        RecyclerView.ViewHolder(specialWinnerView.root)
}
