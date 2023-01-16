package net.sharetrip.flight.history.view.rule

import android.annotation.SuppressLint
import android.graphics.Color
import android.graphics.Typeface
import android.view.LayoutInflater
import android.view.ViewGroup
import android.widget.LinearLayout
import android.widget.TextView
import androidx.databinding.DataBindingUtil
import androidx.recyclerview.widget.RecyclerView
import net.sharetrip.flight.R
import net.sharetrip.flight.databinding.ItemBasicBaggageBinding
import net.sharetrip.flight.history.model.BasicItem

class BasicBaggageAdapter : RecyclerView.Adapter<BasicBaggageAdapter.BasicBaggageViewHolder>() {

    private val basicBaggageList: MutableList<BasicItem> = ArrayList()

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): BasicBaggageViewHolder {
        val binding = DataBindingUtil.inflate<ItemBasicBaggageBinding>(
            LayoutInflater.from(parent.context),
            R.layout.item_basic_baggage,
            parent,
            false
        )
        return BasicBaggageViewHolder(binding)
    }

    @SuppressLint("SetTextI18n")
    override fun onBindViewHolder(holder: BasicBaggageViewHolder, position: Int) {
        val basicItem = basicBaggageList[position]
        holder.binding.textViewRoute.text =
            basicItem.origin.code + " - " + basicItem.destination.code

        basicItem.baggage.forEach {
            val linearLayout = LinearLayout(holder.itemView.context)
            linearLayout.orientation = LinearLayout.HORIZONTAL

            val params = LinearLayout.LayoutParams(
                LinearLayout.LayoutParams.MATCH_PARENT, LinearLayout.LayoutParams.WRAP_CONTENT
            )
            params.setMargins(0, 16, 0, 0)
            linearLayout.layoutParams = params

            val baggageHeader = TextView(holder.itemView.context)
            baggageHeader.text = it.name + ": "
            baggageHeader.setTextColor(Color.parseColor("#000000"))
            linearLayout.addView(baggageHeader)

            val baggageDetails = TextView(holder.itemView.context)
            baggageDetails.typeface = Typeface.DEFAULT_BOLD
            baggageDetails.text = it.weight.toString() + " " + it.unit
            baggageDetails.setTextColor(Color.parseColor("#000000"))
            linearLayout.addView(baggageDetails)

            holder.binding.layoutContainer.addView(linearLayout)
        }

    }

    override fun getItemCount() = basicBaggageList.size

    @SuppressLint("NotifyDataSetChanged")
    fun setBaggage(list: List<BasicItem>) {
        this.basicBaggageList.addAll(list)
        notifyDataSetChanged()
    }

    inner class BasicBaggageViewHolder(val binding: ItemBasicBaggageBinding) :
        RecyclerView.ViewHolder(binding.root)

}