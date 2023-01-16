package net.sharetrip.flight.booking.view.passenger.mealWheelchair

import android.graphics.Color
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.view.ViewGroup.MarginLayoutParams
import androidx.databinding.DataBindingUtil
import androidx.lifecycle.MutableLiveData
import androidx.recyclerview.widget.RecyclerView
import net.sharetrip.flight.R
import net.sharetrip.flight.booking.model.Ssr
import net.sharetrip.flight.databinding.ItemMealWheelchairLayoutFlightBinding

class MealWheelchairAdapter(
    private val ssrList: ArrayList<Ssr>,
    private var selectedSsr: String,
    private val viewModel: MutableLiveData<Pair<String, String>>
) : RecyclerView.Adapter<MealWheelchairAdapter.NotificationViewHolder>() {

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): NotificationViewHolder {
        val viewHolder = DataBindingUtil.inflate<ItemMealWheelchairLayoutFlightBinding>(
            LayoutInflater.from(parent.context),
            R.layout.item_meal_wheelchair_layout_flight, parent, false
        )
        return NotificationViewHolder(viewHolder)
    }

    override fun getItemCount(): Int = ssrList.size

    override fun onBindViewHolder(holder: NotificationViewHolder, position: Int) {
        val ssrItem = ssrList[position]
        holder.binding.viewModel = ssrItem
        if (ssrItem.name == selectedSsr) {
            holder.binding.itemName.setTextColor(Color.parseColor("#1882ff"))
            holder.binding.doneIcon.visibility = View.VISIBLE

        } else {
            holder.binding.itemName.setTextColor(Color.parseColor("#4c4c4c"))
            holder.binding.doneIcon.visibility = View.GONE
        }
        when (position) {
            0 -> {
                setMargins(holder.binding.root, 0, 50, 0, 0)
            }
            else -> {
                setMargins(holder.binding.root, 0, 0, 0, 0)
            }
        }
        holder.binding.root.setOnClickListener {
            selectedSsr = ssrItem.name
            notifyDataSetChanged()
            viewModel.value = Pair(selectedSsr, ssrItem.code)
        }
    }

    private fun setMargins(view: View, left: Int, top: Int, right: Int, bottom: Int) {
        val density = view.context.resources.displayMetrics.density
        val newTop = (top * density).toInt()
        if (view.layoutParams is MarginLayoutParams) {
            val p = view.layoutParams as MarginLayoutParams
            p.setMargins(left, newTop, right, bottom)
            view.requestLayout()
        }
    }

    class NotificationViewHolder(val binding: ItemMealWheelchairLayoutFlightBinding) :
        RecyclerView.ViewHolder(binding.root)
}