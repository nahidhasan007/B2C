package net.sharetrip.holiday.booking.view.summary

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
import com.example.holiday.R
import com.example.holiday.databinding.ItemPaymentMethodHolidayBinding
import net.sharetrip.payment.model.PaymentMethod
import net.sharetrip.payment.model.Series
import java.util.*

class PaymentAdapter : RecyclerView.Adapter<PaymentAdapter.PaymentIconViewHolder>() {

    private val paymentMethodList = ArrayList<PaymentMethod>()

    var selectedPaymentId = ""
    var selectedPaymentMethod = MutableLiveData<PaymentMethod>()
    var series: ArrayList<Series>? = null

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): PaymentIconViewHolder {
        val itemView = DataBindingUtil.inflate<ItemPaymentMethodHolidayBinding>(LayoutInflater.from(parent.context),
                R.layout.item_payment_method_holiday, parent, false)
        return PaymentIconViewHolder(itemView)
    }

    override fun getItemCount() = paymentMethodList.size

    override fun onBindViewHolder(holder: PaymentIconViewHolder, position: Int) {
        holder.bind(paymentMethodList[position])
    }

    fun update(paymentList: ArrayList<PaymentMethod>) {
        paymentMethodList.clear()

        if (paymentList.isEmpty()) {
            notifyDataSetChanged()
            return
        }

        paymentMethodList.addAll(paymentList)
        series = paymentMethodList[0].series as ArrayList<Series>
        selectedPaymentId = paymentMethodList[0].id
        notifyDataSetChanged()
    }

    fun selectedItem(paymentMethod: PaymentMethod) {
        series = paymentMethod.series as ArrayList<Series>
        selectedPaymentId = paymentMethod.id
        selectedPaymentMethod.value = paymentMethod
        notifyDataSetChanged()
    }

    inner class PaymentIconViewHolder(private val bindingView: ItemPaymentMethodHolidayBinding) : RecyclerView.ViewHolder(bindingView.root) {
        fun bind(paymentMethod: PaymentMethod) {
            Glide.with(bindingView.iconPaymentMethod)
                    .load(paymentMethod.logo.small)
                    .apply(RequestOptions.bitmapTransform(CenterInside()))
                    .into(bindingView.iconPaymentMethod)

            if (selectedPaymentId == paymentMethod.id) {
                val gradient = bindingView.iconPaymentMethod.background as GradientDrawable
                gradient.setStroke(6, ContextCompat.getColor(bindingView.iconPaymentMethod.context, R.color.colorPrimary))
            } else {
                val gradient = bindingView.iconPaymentMethod.background as GradientDrawable
                gradient.setStroke(6, ContextCompat.getColor(bindingView.iconPaymentMethod.context, R.color.gray_800))
            }

            bindingView.iconPaymentMethod.setOnClickListener {
                selectedItem(paymentMethod)
            }
        }
    }
}
