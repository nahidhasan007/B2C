package net.sharetrip.hotel.booking.view.summary

import android.annotation.SuppressLint
import android.graphics.drawable.GradientDrawable
import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.core.content.ContextCompat
import androidx.databinding.DataBindingUtil
import androidx.lifecycle.MutableLiveData
import androidx.recyclerview.widget.RecyclerView
import com.bumptech.glide.Glide
import com.bumptech.glide.load.resource.bitmap.CenterInside
import com.bumptech.glide.request.RequestOptions
import net.sharetrip.hotel.R
import net.sharetrip.hotel.booking.model.payment.PaymentMethod
import net.sharetrip.hotel.booking.model.payment.Series
import net.sharetrip.hotel.databinding.ItemPaymentMethodHotelBinding

class PaymentAdapter : RecyclerView.Adapter<PaymentAdapter.PaymentViewHolder>() {
    private val paymentMethodList = ArrayList<PaymentMethod>()
    var selectedPaymentId = ""
    var selectedPaymentMethod = MutableLiveData<PaymentMethod>()
    var series: ArrayList<Series>? = null

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): PaymentViewHolder {
        val itemView =
            DataBindingUtil.inflate<ItemPaymentMethodHotelBinding>(
                LayoutInflater.from(parent.context),
                R.layout.item_payment_method_hotel, parent, false
            )
        return PaymentViewHolder(itemView)
    }

    override fun getItemCount() = paymentMethodList.size

    override fun onBindViewHolder(holder: PaymentViewHolder, position: Int) {
        holder.bind(paymentMethodList[position])
    }

    @SuppressLint("NotifyDataSetChanged")
    fun update(methods: List<PaymentMethod>) {
        if (methods.isEmpty())
            return
        paymentMethodList.clear()
        paymentMethodList.addAll(methods)
        if (paymentMethodList.isNotEmpty())
            series = paymentMethodList[0].series as ArrayList<Series>?
        selectedPaymentId = paymentMethodList[0].id
        selectedPaymentMethod.value = paymentMethodList[0]
        notifyDataSetChanged()
    }

    inner class PaymentViewHolder(private val bindingView: ItemPaymentMethodHotelBinding) :
        RecyclerView.ViewHolder(bindingView.root) {

        fun bind(paymentMethod: PaymentMethod) {
            Glide.with(bindingView.iconPaymentMethod)
                .load(paymentMethod.logo.small)
                .apply(RequestOptions.bitmapTransform(CenterInside()))
                .into(bindingView.iconPaymentMethod)

            if (selectedPaymentId == paymentMethod.id) {
                val gradient = bindingView.iconPaymentMethod.background as GradientDrawable
                gradient.setStroke(
                    2,
                    ContextCompat.getColor(
                        bindingView.iconPaymentMethod.context,
                        R.color.colorPrimary
                    )
                )
            } else {
                val gradient = bindingView.iconPaymentMethod.background as GradientDrawable
                gradient.setStroke(
                    2,
                    ContextCompat.getColor(bindingView.iconPaymentMethod.context, R.color.gray_800)
                )
            }

            bindingView.iconPaymentMethod.setOnClickListener {
                selectedItem(paymentMethod)
            }
        }
    }

    @SuppressLint("NotifyDataSetChanged")
    fun selectedItem(paymentMethod: PaymentMethod) {
        series = paymentMethod.series as ArrayList<Series>
        selectedPaymentId = paymentMethod.id
        notifyDataSetChanged()
        selectedPaymentMethod.value = paymentMethod
    }
}
