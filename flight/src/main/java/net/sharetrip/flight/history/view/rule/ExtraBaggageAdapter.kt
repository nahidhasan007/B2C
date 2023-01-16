package net.sharetrip.flight.history.view.rule

import android.annotation.SuppressLint
import android.graphics.Color
import android.graphics.Typeface
import android.view.Gravity
import android.view.LayoutInflater
import android.view.ViewGroup
import android.widget.LinearLayout
import android.widget.TextView
import androidx.databinding.DataBindingUtil
import androidx.recyclerview.widget.RecyclerView
import net.sharetrip.flight.R
import net.sharetrip.flight.databinding.ItemExtraBaggageBinding
import net.sharetrip.flight.history.model.ExtraItem
import java.text.NumberFormat
import java.util.*
import kotlin.collections.ArrayList


class ExtraBaggageAdapter : RecyclerView.Adapter<ExtraBaggageAdapter.BasicBaggageViewHolder>() {

    private val extraBaggageList: MutableList<ExtraItem> = ArrayList()

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): BasicBaggageViewHolder {
        val binding = DataBindingUtil.inflate<ItemExtraBaggageBinding>(
            LayoutInflater.from(parent.context),
            R.layout.item_extra_baggage,
            parent,
            false
        )
        return BasicBaggageViewHolder(binding)
    }

    @SuppressLint("SetTextI18n")
    override fun onBindViewHolder(holder: BasicBaggageViewHolder, position: Int) {
        val extraItem = extraBaggageList[position]
        holder.binding.textViewRoute.text = extraItem.route

        if (extraItem.details.isNullOrEmpty()) {
            val linearLayout = LinearLayout(holder.itemView.context)
            linearLayout.orientation = LinearLayout.HORIZONTAL

            val params = LinearLayout.LayoutParams(
                LinearLayout.LayoutParams.MATCH_PARENT, LinearLayout.LayoutParams.WRAP_CONTENT
            )
            params.setMargins(0, 16, 0, 0)
            linearLayout.layoutParams = params

            val baggageHeader = TextView(holder.itemView.context)
            baggageHeader.text = "No baggage added for this flight"
            baggageHeader.setTextColor(Color.parseColor("#000000"))
            linearLayout.addView(baggageHeader)
            holder.binding.layoutContainer.addView(linearLayout)

        } else {
            extraItem.details.forEach {
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
                baggageDetails.text = it.weight
                baggageDetails.setTextColor(Color.parseColor("#000000"))
                linearLayout.addView(baggageDetails)

                val param = LinearLayout.LayoutParams(
                    LinearLayout.LayoutParams.MATCH_PARENT, LinearLayout.LayoutParams.WRAP_CONTENT
                )
                val priceTextView = TextView(holder.itemView.context)
                priceTextView.typeface = Typeface.DEFAULT_BOLD
                priceTextView.text =
                    "BDT ${NumberFormat.getNumberInstance(Locale.US).format(it.price)}"
                priceTextView.layoutParams = param
                priceTextView.gravity = Gravity.RIGHT
                priceTextView.setTextColor(Color.parseColor("#000000"))
                linearLayout.addView(priceTextView)

                holder.binding.layoutContainer.addView(linearLayout)
            }
        }
    }

    override fun getItemCount() = extraBaggageList.size

    @SuppressLint("NotifyDataSetChanged")
    fun setBaggage(list: List<ExtraItem>) {
        this.extraBaggageList.addAll(list)
        notifyDataSetChanged()
    }

    inner class BasicBaggageViewHolder(val binding: ItemExtraBaggageBinding) :
        RecyclerView.ViewHolder(binding.root)

}