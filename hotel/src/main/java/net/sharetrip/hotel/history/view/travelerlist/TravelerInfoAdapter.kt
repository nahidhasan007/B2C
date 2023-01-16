package net.sharetrip.hotel.history.view.travelerlist

import android.annotation.SuppressLint
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.databinding.DataBindingUtil
import androidx.recyclerview.widget.RecyclerView
import net.sharetrip.hotel.R
import net.sharetrip.hotel.booking.model.RoomGuestType
import net.sharetrip.hotel.booking.model.RoomNo
import net.sharetrip.hotel.databinding.ItemHotelHistoryGuestRoomNumberBinding
import net.sharetrip.hotel.databinding.ItemHotelHistoryTravellersDetailBinding
import net.sharetrip.hotel.history.model.RoomsItem

class TravelerInfoAdapter(private val guestList: List<RoomsItem>, val roomNo: Int) :
    RecyclerView.Adapter<RecyclerView.ViewHolder>() {

    private val TYPE_HEADER = 0
    private val TYPE_ITEM = 1
    private var adultCount = 0
    private var childCount = 0

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): RecyclerView.ViewHolder {

        if (viewType == TYPE_HEADER) {
            val bindingView =
                DataBindingUtil.inflate<ItemHotelHistoryGuestRoomNumberBinding>(LayoutInflater.from(
                    parent.context), R.layout.item_hotel_history_guest_room_number, parent, false)

            return HotelInfoVerificationHeaderHolder(bindingView)

        } else if (viewType == TYPE_ITEM) {
            val bindingView =
                DataBindingUtil.inflate<ItemHotelHistoryTravellersDetailBinding>(LayoutInflater.from(
                    parent.context), R.layout.item_hotel_history_travellers_detail, parent, false)

            return HotelInfoVerificationItemHolder(bindingView)
        }

        throw RuntimeException("There is no view type matches")
    }

    override fun getItemCount() = guestList.size + 1

    override fun getItemViewType(position: Int): Int =
        if (isPositionHeader(position)) TYPE_HEADER else TYPE_ITEM

    private fun isPositionHeader(position: Int): Boolean = position == 0

    private fun getItem(position: Int): RoomsItem? = guestList?.get(position - 1)

    @SuppressLint("SetTextI18n")
    override fun onBindViewHolder(holder: RecyclerView.ViewHolder, holderPosition: Int) {

        if (holder is HotelInfoVerificationHeaderHolder) {
            holder.bindingView.roomNo = RoomNo(roomNo)

        } else if (holder is HotelInfoVerificationItemHolder) {
            try {
                val guestInfo = getItem(holderPosition)
                holder.bindingView.guest = guestInfo

                if (guestInfo?.travellerType == RoomGuestType.ARG_ADULT) {
                    adultCount++
                    holder.bindingView.typeOfUser.text = guestInfo?.travellerType + " " + adultCount
                } else {
                    childCount++
                    holder.bindingView.typeOfUser.text = guestInfo?.travellerType + " " + childCount
                }

                guestInfo?.age?.let {
                    holder.bindingView.relativeAge.visibility = View.VISIBLE
                    holder.bindingView.tvChildAge.text = guestInfo?.age
                }
            } catch (e: Exception) {
                e.printStackTrace()
            }
        }
    }

    inner class HotelInfoVerificationItemHolder(val bindingView: ItemHotelHistoryTravellersDetailBinding) :
        RecyclerView.ViewHolder(bindingView.root)

    inner class HotelInfoVerificationHeaderHolder(val bindingView: ItemHotelHistoryGuestRoomNumberBinding) :
        RecyclerView.ViewHolder(bindingView.root)
}
