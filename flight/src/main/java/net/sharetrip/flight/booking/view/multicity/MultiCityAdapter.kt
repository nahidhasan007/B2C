package net.sharetrip.flight.booking.view.multicity

import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.databinding.DataBindingUtil
import androidx.recyclerview.widget.RecyclerView
import net.sharetrip.shared.utils.parseDateForDisplayFromApi
import net.sharetrip.flight.R
import net.sharetrip.flight.booking.model.DestinationPath
import net.sharetrip.flight.booking.model.MultiCityModel
import net.sharetrip.flight.databinding.ItemMultiCityBinding

class MultiCityAdapter(
    private var dataList: MutableList<MultiCityModel>,
    private val formListener: (Int) -> Unit,
    private val toListener: (Int) -> Unit,
    private val dateListener: (Int, String, String) -> Unit
) : RecyclerView.Adapter<MultiCityAdapter.MultiCityViewHolder>() {
    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): MultiCityViewHolder {
        val inflater = LayoutInflater.from(parent.context)
        val cityBinding = DataBindingUtil.inflate<ItemMultiCityBinding>(
            inflater,
            R.layout.item_multi_city, parent, false
        )
        return MultiCityViewHolder(cityBinding)
    }

    override fun onBindViewHolder(holder: MultiCityViewHolder, position: Int) {
        val item = dataList[position]
        val path = DestinationPath(
            item.origin,
            item.destination,
            item.departDate.parseDateForDisplayFromApi()
        )
        holder.multiCityBinding.data = path

        holder.multiCityBinding.fromLayout.setOnClickListener {
            formListener.invoke(position)
        }

        holder.multiCityBinding.toLayout.setOnClickListener {
            toListener.invoke(position)
        }

        holder.multiCityBinding.departureDateLayout.setOnClickListener {
            dateListener(
                position,
                holder.multiCityBinding.fromCodeTextView.text.toString(),
                holder.multiCityBinding.toCodeTextView.text.toString()
            )
        }
    }

    override fun getItemCount(): Int = dataList.size

    fun addItem() {
        dataList.add(MultiCityModel())
        notifyDataSetChanged()
    }

    fun removeItem() {
        dataList.removeLast()
        notifyDataSetChanged()
    }

    fun resetItems(newList: List<MultiCityModel>) {
        dataList = newList as MutableList<MultiCityModel>
    }

    fun changeItemAtPos(pos: Int, item: MultiCityModel) {
        if (pos in 0 until dataList.size) {
            dataList[pos] = item
            notifyItemChanged(pos)
        }
    }

    inner class MultiCityViewHolder(val multiCityBinding: ItemMultiCityBinding) :
        RecyclerView.ViewHolder(multiCityBinding.root)
}
