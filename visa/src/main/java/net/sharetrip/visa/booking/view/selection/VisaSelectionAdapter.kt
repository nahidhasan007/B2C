package net.sharetrip.visa.booking.view.selection

import android.annotation.SuppressLint
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.databinding.DataBindingUtil
import androidx.recyclerview.widget.RecyclerView
import net.sharetrip.visa.R
import net.sharetrip.visa.booking.model.VisaProductsItem
import net.sharetrip.visa.databinding.ItemVisaSelectionBinding
import java.text.NumberFormat
import java.util.*

class VisaSelectionAdapter(var listener: SingleClickListener) :
    RecyclerView.Adapter<VisaSelectionAdapter.VisaSelectionViewHolder>() {

    var lastSelectedPosition = -1
    private val visaProductList: MutableList<VisaProductsItem> = mutableListOf()

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): VisaSelectionViewHolder {
        val bindingItem = DataBindingUtil.inflate<ItemVisaSelectionBinding>(
            LayoutInflater.from(parent.context),
            R.layout.item_visa_selection, parent, false
        )
        return VisaSelectionViewHolder(bindingItem)
    }

    override fun getItemCount() = visaProductList.size

    @SuppressLint("SetTextI18n")
    override fun onBindViewHolder(holder: VisaSelectionViewHolder, position: Int) {
        val visaItem = visaProductList[position]
        holder.bindingView.radioVisaType.text = visaItem.title
        holder.bindingView.tvValidity.text = visaItem.validityDays.toString() + " Days"
        holder.bindingView.tvMaxStay.text = visaItem.maxStayDays.toString() + " Days"
        holder.bindingView.tvPrice.text =
            "BDT " + NumberFormat.getNumberInstance(Locale.US).format(visaItem.visaFee)
        holder.bindingView.radioVisaType.isChecked = lastSelectedPosition == position
    }

    fun updateData(arrayList: List<VisaProductsItem>?) {
        visaProductList.clear()
        visaProductList.addAll(arrayList ?: return)
        notifyDataSetChanged()
    }

    inner class VisaSelectionViewHolder(val bindingView: ItemVisaSelectionBinding) :
        RecyclerView.ViewHolder(bindingView.root), View.OnClickListener {

        init {
            bindingView.root.setOnClickListener(this)
        }

        override fun onClick(v: View?) {
            lastSelectedPosition = absoluteAdapterPosition
            notifyDataSetChanged()
            if (lastSelectedPosition > -1) {
                listener.onServiceSelected(
                    visaProductList[lastSelectedPosition],
                    lastSelectedPosition
                )
            }
        }
    }

    interface SingleClickListener {
        fun onServiceSelected(visaProduct: VisaProductsItem?, lastSelectedPosition: Int)
    }
}
