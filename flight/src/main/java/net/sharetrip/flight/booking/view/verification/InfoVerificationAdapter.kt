package net.sharetrip.flight.booking.view.verification

import android.os.Build
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.annotation.RequiresApi
import androidx.databinding.DataBindingUtil
import androidx.recyclerview.widget.RecyclerView
import com.bumptech.glide.Glide
import net.sharetrip.flight.R
import net.sharetrip.flight.booking.model.ItemTraveler
import net.sharetrip.flight.booking.model.PassengerType
import net.sharetrip.flight.databinding.UserDetailVerificationAdapterOfFlightBinding

class InfoVerificationAdapter(
    var passengerlist: MutableList<ItemTraveler>,
    private val imageListener: ShowImageListener
) : RecyclerView.Adapter<InfoVerificationAdapter.UserInfoViewHolder>() {

    @RequiresApi(Build.VERSION_CODES.LOLLIPOP)
    override fun onBindViewHolder(holder: UserInfoViewHolder, position: Int) {
        holder.binding.passenger = passengerlist[position]

        if (!passengerlist[position].passportCopy.isNullOrEmpty()) {
            holder.binding.imgPassportCopyNoImage.visibility = View.INVISIBLE
            holder.binding.imgPassportCopy.visibility = View.VISIBLE
            Glide.with(holder.itemView.context)
                .load(passengerlist[position].passportCopy)
                .into(holder.binding.imgPassportCopy)
        } else {
            holder.binding.imgPassportCopyNoImage.visibility = View.VISIBLE
            holder.binding.imgPassportCopy.visibility = View.INVISIBLE
        }

        if (!passengerlist[position].visaCopy.isNullOrEmpty()) {
            holder.binding.imgVisaCopyNoImage.visibility = View.INVISIBLE
            holder.binding.imgVisaCopy.visibility = View.VISIBLE
            Glide.with(holder.itemView.context)
                .load(passengerlist[position].visaCopy)

                .into(holder.binding.imgVisaCopy)
        } else {
            holder.binding.imgVisaCopyNoImage.visibility = View.VISIBLE
            holder.binding.imgVisaCopy.visibility = View.INVISIBLE
        }

        if (passengerlist[position].gender == "Male") {
            holder.binding.imgUsergender.setImageDrawable(holder.itemView.context.getDrawable(R.drawable.ic_male_mono))
        } else if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
            holder.binding.imgUsergender.setImageDrawable(holder.itemView.context.getDrawable(R.drawable.ic_female_mono))
        }

        if (!passengerlist[position].passportCopy.isNullOrEmpty()) {
            holder.binding.imgPassportCopy.setOnClickListener {
                passengerlist[position].passportCopy?.let { it1 ->
                    imageListener.showImage(
                        it1,
                        "passport"
                    )
                }
            }
        }

        if (!passengerlist[position].visaCopy.isNullOrEmpty()) {
            holder.binding.imgVisaCopy.setOnClickListener {
                passengerlist[position].visaCopy?.let { it1 ->
                    imageListener.showImage(
                        it1,
                        "visa"
                    )
                }
            }
        }

        holder.binding.baggageInsuranceLayout.visibility =
            if (passengerlist[position].passengerType == PassengerType.INFANT) View.GONE else View.VISIBLE

    }

    override fun getItemCount(): Int = passengerlist.size

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): UserInfoViewHolder {
        val viewHolder: RecyclerView.ViewHolder
        val inflater = LayoutInflater.from(parent.context)
        val binding = DataBindingUtil.inflate<UserDetailVerificationAdapterOfFlightBinding>(
            inflater,
            R.layout.user_detail_verification_adapter_of_flight,
            parent,
            false
        )
        viewHolder = UserInfoViewHolder(binding)
        return viewHolder
    }

    inner class UserInfoViewHolder(var binding: UserDetailVerificationAdapterOfFlightBinding) :
        RecyclerView.ViewHolder(binding.root)
}
