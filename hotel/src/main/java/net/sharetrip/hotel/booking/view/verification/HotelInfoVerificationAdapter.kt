package net.sharetrip.hotel.booking.view.verification

import android.annotation.SuppressLint
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.databinding.DataBindingUtil
import androidx.recyclerview.widget.RecyclerView
import net.sharetrip.hotel.R
import net.sharetrip.hotel.booking.model.GuestInfo
import net.sharetrip.hotel.booking.model.RoomGuestType
import net.sharetrip.hotel.booking.model.RoomNo
import net.sharetrip.hotel.databinding.ItemGuestRoomNumberBinding
import net.sharetrip.hotel.databinding.ItemHotelUserDetailVerificationBinding

class HotelInfoVerificationAdapter(private val guestList: ArrayList<GuestInfo>, val roomNo: Int) :
    RecyclerView.Adapter<RecyclerView.ViewHolder>() {
    private val typeHeader = 0
    private val typeItem = 1
    private var adultCount = 0
    private var childCount = 0

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): RecyclerView.ViewHolder {

        if (viewType == typeHeader) {
            val bindingView =
                DataBindingUtil.inflate<ItemGuestRoomNumberBinding>(
                    LayoutInflater.from(parent.context),
                    R.layout.item_guest_room_number,
                    parent,
                    false
                )

            return HotelInfoVerificationHeaderHolder(bindingView)
        } else if (viewType == typeItem) {
            val bindingView =
                DataBindingUtil.inflate<ItemHotelUserDetailVerificationBinding>(
                    LayoutInflater.from(
                        parent.context
                    ), R.layout.item_hotel_user_detail_verification, parent, false
                )

            return HotelInfoVerificationItemHolder(bindingView)
        }

        throw RuntimeException("There is no view type matches")
    }

    override fun getItemCount() = guestList.size + 1

    override fun getItemViewType(position: Int): Int =
        if (isPositionHeader(position)) typeHeader else typeItem

    private fun isPositionHeader(position: Int): Boolean = position == 0

    private fun getItem(position: Int): GuestInfo = guestList[position - 1]

    @SuppressLint("SetTextI18n")
    override fun onBindViewHolder(holder: RecyclerView.ViewHolder, holderPosition: Int) {

        if (holder is HotelInfoVerificationHeaderHolder) {
            holder.bindingView.roomNo = RoomNo(roomNo)

        } else if (holder is HotelInfoVerificationItemHolder) {
            val guestInfo = getItem(holderPosition)
            holder.bindingView.guest = guestInfo

            if (guestInfo.type == RoomGuestType.ARG_ADULT) {
                adultCount++
                holder.bindingView.relativeAge.visibility = View.GONE
                holder.bindingView.typeOfUser.text = guestInfo.type + " " + adultCount
            } else {
                childCount++
                holder.bindingView.typeOfUser.text = guestInfo.type + " " + childCount
            }
        }
    }

    inner class HotelInfoVerificationItemHolder(val bindingView: ItemHotelUserDetailVerificationBinding) :
        RecyclerView.ViewHolder(bindingView.root)

    inner class HotelInfoVerificationHeaderHolder(val bindingView: ItemGuestRoomNumberBinding) :
        RecyclerView.ViewHolder(bindingView.root)
}
