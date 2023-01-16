package net.sharetrip.tour.booking.summary

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
import net.sharetrip.payment.model.PaymentMethod
import net.sharetrip.tour.R
import net.sharetrip.tour.databinding.ItemPaymentMethodTourBinding

class PaymentAdapter : RecyclerView.Adapter<PaymentAdapter.PaymentIconViewHolder>() {

    private val paymentMethodList = ArrayList<net.sharetrip.payment.model.PaymentMethod>()
    var selectedPaymentId = MutableLiveData<String>()

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): PaymentIconViewHolder {
        val itemView = DataBindingUtil.inflate<ItemPaymentMethodTourBinding>(
            LayoutInflater.from(parent.context),
            R.layout.item_payment_method_tour, parent, false
        )
        return PaymentIconViewHolder(itemView)
    }

    override fun getItemCount() = paymentMethodList.size

    override fun onBindViewHolder(holder: PaymentIconViewHolder, position: Int) {
        holder.bind(paymentMethodList[position])
    }

    fun update(paymentList: ArrayList<net.sharetrip.payment.model.PaymentMethod>) {
        paymentMethodList.clear()
        paymentMethodList.addAll(paymentList)
        notifyDataSetChanged()
    }

    fun selectedItem(paymentMethod: net.sharetrip.payment.model.PaymentMethod) {
        selectedPaymentId.value = paymentMethod.id
        notifyDataSetChanged()
    }

    inner class PaymentIconViewHolder(private val bindingView: ItemPaymentMethodTourBinding) :
        RecyclerView.ViewHolder(bindingView.root) {
        fun bind(paymentMethod: net.sharetrip.payment.model.PaymentMethod) {
            Glide.with(bindingView.iconPaymentMethod)
                .load(paymentMethod.logo.small)
                .apply(RequestOptions.bitmapTransform(CenterInside()))
                .into(bindingView.iconPaymentMethod)

            if (selectedPaymentId.value == paymentMethod.id) {
                val gradient = bindingView.iconPaymentMethod.background as GradientDrawable
                gradient.setStroke(
                    6,
                    ContextCompat.getColor(
                        bindingView.iconPaymentMethod.context,
                        R.color.colorPrimary
                    )
                )
            } else {
                val gradient = bindingView.iconPaymentMethod.background as GradientDrawable
                gradient.setStroke(
                    6,
                    ContextCompat.getColor(bindingView.iconPaymentMethod.context, R.color.gray_800)
                )
            }

            bindingView.iconPaymentMethod.setOnClickListener {
                selectedItem(paymentMethod)
            }
        }
    }
}
