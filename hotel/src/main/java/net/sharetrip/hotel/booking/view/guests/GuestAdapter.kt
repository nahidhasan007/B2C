package net.sharetrip.hotel.booking.view.guests

import android.content.Context
import android.view.LayoutInflater
import android.view.View.GONE
import android.view.ViewGroup
import android.widget.ArrayAdapter
import androidx.databinding.DataBindingUtil
import androidx.recyclerview.widget.RecyclerView
import net.sharetrip.hotel.R
import net.sharetrip.hotel.booking.model.GuestInfo
import net.sharetrip.hotel.booking.model.RoomGuestType
import net.sharetrip.hotel.booking.model.Traveler
import net.sharetrip.hotel.databinding.ItemGuestInfoBinding

class GuestAdapter(private val viewModel: RoomGuestViewModel) :
    RecyclerView.Adapter<GuestAdapter.GuestInfoViewHolder>() {
    private val guestList = ArrayList<GuestInfo>()
    private val passengerList = ArrayList<Traveler>()
    private lateinit var context: Context
    private var adultCount = 0
    private var childCount = 0

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): GuestInfoViewHolder {
        context = parent.context
        val bindingView =
            DataBindingUtil.inflate<ItemGuestInfoBinding>(
                LayoutInflater.from(context),
                R.layout.item_guest_info,
                parent,
                false
            )
        return GuestInfoViewHolder(bindingView)
    }

    override fun getItemCount() = guestList.size

    override fun onBindViewHolder(holder: GuestInfoViewHolder, holderPosition: Int) {
        val title = context.resources.getStringArray(R.array.hotel_person_title)
        val adapter = ArrayAdapter(context, android.R.layout.simple_dropdown_item_1line, title)
        holder.bindingView.textFieldTitle.setAdapter(adapter)

        if (guestList[holderPosition].type == null || guestList[holderPosition].type == RoomGuestType.ARG_ADULT) {
            adultCount += 1
            val viewModel = GuestViewModel(guestList[holderPosition], adultCount)
            holder.bindingView.viewModel = viewModel

            val givenNameList =
                passengerList.filter { it.travellerType == null || it.travellerType == RoomGuestType.ARG_ADULT || it.travellerType == "" }
                    .map { it.surName }

            if (givenNameList.isNotEmpty()) {
                val nameAdapter = ArrayAdapter(
                    context,
                    android.R.layout.simple_dropdown_item_1line,
                    givenNameList
                )
                holder.bindingView.quickPickAutoCompleteTextView.setAdapter(nameAdapter)
                holder.bindingView.quickPickAutoCompleteTextView.setOnItemClickListener { _, _, position, _ ->
                    viewModel.setQuickPickerData(guestList[holderPosition], passengerList[position])
                    holder.bindingView.viewModel = viewModel
                }
            } else {
                holder.bindingView.quickPickAutoCompleteTextView.visibility = GONE
                holder.bindingView.imageViewQuickPick.visibility = GONE
            }

        } else {
            childCount += 1
            val viewModel = GuestViewModel(guestList[holderPosition], childCount)
            holder.bindingView.viewModel = viewModel
            val givenNameList = passengerList.filter { it.travellerType == RoomGuestType.ARG_CHILD }
                .map { it.givenName }
            if (givenNameList.isNotEmpty()) {
                val nameAdapter = ArrayAdapter(
                    context,
                    android.R.layout.simple_dropdown_item_1line,
                    givenNameList
                )
                holder.bindingView.quickPickAutoCompleteTextView.setAdapter(nameAdapter)
                holder.bindingView.quickPickAutoCompleteTextView.setOnItemClickListener { _, _, position, _ ->
                    viewModel.setQuickPickerData(guestList[holderPosition], passengerList[position])
                    holder.bindingView.viewModel = viewModel
                }
            } else {
                holder.bindingView.quickPickAutoCompleteTextView.visibility = GONE
                holder.bindingView.imageViewQuickPick.visibility = GONE
            }
        }

        holder.bindingView.textFieldBirthDay.keyListener = null
        holder.bindingView.textFieldBirthDay.setOnClickListener {
            viewModel.onClickNationality(holderPosition)
        }
        holder.bindingView.textFieldBirthDay.setOnFocusChangeListener { _, hasFocus ->
            if (hasFocus) {
                viewModel.onClickNationality(holderPosition)
                holder.bindingView.textFieldBirthDay.isFocusable = false
            }
        }

    }

    fun update(guests: ArrayList<GuestInfo>, passenger: ArrayList<Traveler>) {
        adultCount = 0
        childCount = 0
        passengerList.clear()
        passengerList.addAll(passenger)
        guestList.clear()
        guestList.addAll(guests)
        notifyDataSetChanged()
    }

    inner class GuestInfoViewHolder(val bindingView: ItemGuestInfoBinding) :
        RecyclerView.ViewHolder(bindingView.root)
}
