package net.sharetrip.flight.booking.view.passenger.covidTest

import android.text.Editable
import android.text.TextWatcher
import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import net.sharetrip.flight.booking.model.CovidTestOption
import net.sharetrip.flight.databinding.ItemCovidAddOnsFlightBinding

class CovidAdapter(
    private val covidAddOnList: ArrayList<CovidTestOption>,
    private var selectedCode: String,
    val listener: CovidAddOnsListener
) : RecyclerView.Adapter<CovidAdapter.CovidViewHolder>() {
    var selectedPosition = -1
    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): CovidViewHolder {
        val viewHolder =
            ItemCovidAddOnsFlightBinding.inflate(LayoutInflater.from(parent.context), parent, false)
        return CovidViewHolder(viewHolder)
    }

    override fun getItemCount() = covidAddOnList.size

    inner class CovidViewHolder(val binding: ItemCovidAddOnsFlightBinding) :
        RecyclerView.ViewHolder(binding.root)

    interface CovidAddOnsListener {
        fun onClickItem(option: CovidTestOption)
        fun onSetAddress(option: CovidTestOption)
    }

    override fun onBindViewHolder(holder: CovidViewHolder, position: Int) {

        if (selectedCode == covidAddOnList[position].code) {
            selectedPosition = position
        }

        if (position == selectedPosition) {
            listener.onClickItem(covidAddOnList[position])
        }
        covidAddOnList[position].isSelected = position == selectedPosition

        holder.binding.covidTestOption = covidAddOnList[position]
        holder.binding.isActive = position == selectedPosition

        holder.binding.root.setOnClickListener {
            val previousSelectedPosition = selectedPosition
            selectedPosition = position
            selectedCode = covidAddOnList[position].code
            notifyItemChanged(previousSelectedPosition)
            notifyItemChanged(position)
        }

        holder.binding.editTextAddress.addTextChangedListener(object : TextWatcher {
            override fun onTextChanged(s: CharSequence, start: Int, before: Int, count: Int) {

            }

            override fun beforeTextChanged(s: CharSequence, start: Int, count: Int, after: Int) {

            }

            override fun afterTextChanged(s: Editable) {
                covidAddOnList[position].addressDetails = s.toString()
                listener.onSetAddress(covidAddOnList[position])
            }
        })
    }
}