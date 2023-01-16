package net.sharetrip.profile.view.reward

import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.databinding.DataBindingUtil
import androidx.recyclerview.widget.RecyclerView
import net.sharetrip.profile.R
import net.sharetrip.profile.databinding.ItemRewardBoardWinnerBinding
import net.sharetrip.profile.model.RewardWinner
import java.util.*

class SpecialRewardBoardAdapter :
    RecyclerView.Adapter<SpecialRewardBoardAdapter.SpecialRewardViewHolder>() {
    private val specialRewardList = ArrayList<RewardWinner>()

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): SpecialRewardViewHolder {
        val viewHolder = DataBindingUtil.inflate<ItemRewardBoardWinnerBinding>(
            LayoutInflater.from(parent.context),
            R.layout.item_reward_board_winner, parent, false
        )
        return SpecialRewardViewHolder(viewHolder)
    }

    override fun getItemCount() = specialRewardList.size

    override fun onBindViewHolder(holder: SpecialRewardViewHolder, position: Int) {
        val winnerInfo = specialRewardList[position]
        holder.specialWinnerView.rewardWinner = winnerInfo

        when (winnerInfo.type) {
            "Hotel" -> holder.specialWinnerView.iconType.setImageResource(R.drawable.ic_hotel_56dp)
            "Flight" -> holder.specialWinnerView.iconType.setImageResource(R.drawable.ic_flight_56dp)
            "Holiday" -> holder.specialWinnerView.iconType.setImageResource(R.drawable.ic_holiday_light_24dp)
            "Tour" -> holder.specialWinnerView.iconType.setImageResource(R.drawable.ic_tours_56dp)
            "Transport" -> holder.specialWinnerView.iconType.setImageResource(R.drawable.ic_transport_light_24dp)
        }
    }

    fun updateRewardWinnerList(dataList: List<RewardWinner>) {
        if (dataList != null) {
            this.specialRewardList.clear()
            this.specialRewardList.addAll(dataList)
            notifyDataSetChanged()
        }
    }

    class SpecialRewardViewHolder(val specialWinnerView: ItemRewardBoardWinnerBinding) :
        RecyclerView.ViewHolder(specialWinnerView.root)
}
