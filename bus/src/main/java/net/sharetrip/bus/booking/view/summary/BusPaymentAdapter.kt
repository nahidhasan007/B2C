package net.sharetrip.bus.booking.view.summary

import android.view.LayoutInflater
import android.view.ViewGroup
import android.widget.ImageView
import android.widget.LinearLayout
import androidx.databinding.DataBindingUtil
import androidx.recyclerview.widget.RecyclerView
import androidx.viewbinding.ViewBinding
import com.bumptech.glide.Glide
import com.bumptech.glide.load.resource.bitmap.FitCenter
import com.bumptech.glide.request.RequestOptions
import net.sharetrip.bus.R
import net.sharetrip.bus.databinding.ItemPaymentMethodBusBinding

class BusPaymentAdapter(val viewModel: BookingSummaryViewModel) :
    RecyclerView.Adapter<BusPaymentAdapter.ViewHolder>() {

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
        val view = DataBindingUtil.inflate<ItemPaymentMethodBusBinding>(
            LayoutInflater.from(parent.context),
            R.layout.item_payment_method_bus,
            parent,
            false
        )
        return ViewHolder(view)
    }

    override fun onBindViewHolder(holder: ViewHolder, position: Int) {
        val mContext = holder.itemView.context
        val height = mContext.resources.getDimensionPixelSize(R.dimen.payment_logo_height)
        val width = mContext.resources.getDimensionPixelSize(R.dimen.payment_logo_width)
        val padding = mContext.resources.getDimensionPixelSize(R.dimen.spacing_small_tiny)
        val margin = mContext.resources.getDimensionPixelSize(R.dimen.spacing_small)
        holder.itemView.setBackgroundResource(R.drawable.payment_method_selector)
        holder.itemView.layoutParams = LinearLayout.LayoutParams(width, height).apply {
            setMargins(margin, margin, 0, 0)
        }
        holder.itemView.setPadding(padding, padding, padding, padding)
        if (viewModel.paymentMethodListFinal[position].checked)
            holder.itemView.setBackgroundResource(R.drawable.bus_payment_method_selected)
        else
            holder.itemView.setBackgroundResource(R.drawable.bus_payment_method_deselected)
        Glide.with(holder.itemView.context)
            .load(viewModel.paymentMethodListFinal[position].logo.large)
            .apply(RequestOptions.bitmapTransform(FitCenter()))
            .into(holder.itemView as ImageView)
    }

    override fun getItemCount(): Int = viewModel.paymentMethodListFinal.size

    inner class ViewHolder(itemView: ViewBinding) : RecyclerView.ViewHolder(itemView.root)
}
