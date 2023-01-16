package net.sharetrip.flight.history.view.travelerList.adapter

import android.annotation.SuppressLint
import android.os.Build
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.annotation.RequiresApi
import androidx.databinding.DataBindingUtil
import androidx.recyclerview.widget.RecyclerView
import com.bumptech.glide.Glide
import net.sharetrip.flight.R
import net.sharetrip.flight.databinding.HistoryTravellerItemLayoutBinding
import net.sharetrip.flight.history.model.Traveller
import net.sharetrip.flight.history.view.travelerList.TravellerListViewModel

class TravelerHistoryAdapter(
    private val viewModel: TravellerListViewModel,
    private val passengers: ArrayList<Traveller>
) : RecyclerView.Adapter<TravelerHistoryAdapter.UserInfoViewHolder>() {

    var adult = 0
    var child = 0
    var infant = 0

    @SuppressLint("UseCompatLoadingForDrawables", "SetTextI18n")
    @RequiresApi(Build.VERSION_CODES.LOLLIPOP)
    override fun onBindViewHolder(holder: UserInfoViewHolder, position: Int) {
        holder.mBinding.passenger = passengers[position]

        when (passengers[position].travellerType) {
            "Adult" -> {
                adult++
                holder.mBinding.typeOfUser.text = "Adult $adult"
            }
            "Child" -> {
                child++
                holder.mBinding.typeOfUser.text = "Child $child"
            }
            "Infant" -> {
                infant++
                holder.mBinding.typeOfUser.text = "Child $infant"
            }
        }

        if (!passengers[position].passportCopy.isNullOrEmpty()) {
            holder.mBinding.imgPassportCopyNoImage.visibility = View.INVISIBLE
            holder.mBinding.imgPassportCopy.visibility = View.VISIBLE
            Glide.with(holder.itemView.context)
                .load(passengers[position].passportCopy)
                .into(holder.mBinding.imgPassportCopy)
        } else {
            holder.mBinding.imgPassportCopyNoImage.visibility = View.VISIBLE
            holder.mBinding.imgPassportCopy.visibility = View.INVISIBLE
        }

        if (!passengers[position].visaCopy.isNullOrEmpty()) {
            holder.mBinding.imgVisaCopyNoImage.visibility = View.INVISIBLE
            holder.mBinding.imgVisaCopy.visibility = View.VISIBLE
            Glide.with(holder.itemView.context)
                .load(passengers[position].visaCopy)

                .into(holder.mBinding.imgVisaCopy)
        } else {
            holder.mBinding.imgVisaCopyNoImage.visibility = View.VISIBLE
            holder.mBinding.imgVisaCopy.visibility = View.INVISIBLE
        }

        if (passengers[position].gender == "Male") {
            holder.mBinding.imgUsergender.setImageDrawable(holder.itemView.context.getDrawable(R.drawable.ic_male_mono))
        } else if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
            holder.mBinding.imgUsergender.setImageDrawable(holder.itemView.context.getDrawable(R.drawable.ic_female_mono))
        }

        if (!passengers[position].passportCopy.isNullOrEmpty()) {
            holder.mBinding.imgPassportCopy.setOnClickListener {
                passengers[position].passportCopy?.let { it1 ->
                    viewModel.showImage(
                        it1,
                        "passport"
                    )
                }
            }
        }

        if (!passengers[position].visaCopy.isNullOrEmpty()) {
            holder.mBinding.imgVisaCopy.setOnClickListener {
                passengers[position].visaCopy?.let { it1 -> viewModel.showImage(it1, "") }
            }
        }
    }

    override fun getItemCount(): Int = passengers.size

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): UserInfoViewHolder {
        val viewHolder: RecyclerView.ViewHolder
        val inflater = LayoutInflater.from(parent.context)
        val binding = DataBindingUtil.inflate<HistoryTravellerItemLayoutBinding>(
            inflater,
            R.layout.history_traveller_item_layout,
            parent,
            false
        )
        viewHolder = UserInfoViewHolder(binding)
        return viewHolder
    }

    inner class UserInfoViewHolder(var mBinding: HistoryTravellerItemLayoutBinding) :
        RecyclerView.ViewHolder(mBinding.root)
}
