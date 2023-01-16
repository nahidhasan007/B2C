package net.sharetrip.visa.booking.view.application

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.databinding.DataBindingUtil
import androidx.recyclerview.widget.RecyclerView
import net.sharetrip.visa.R
import net.sharetrip.visa.booking.model.VisaFaq
import net.sharetrip.visa.databinding.ItemVisaFaqBinding

class FaqAdapter : RecyclerView.Adapter<FaqAdapter.VisaFaqViewHolder>() {
    private val faqList: MutableList<VisaFaq> = mutableListOf()

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): VisaFaqViewHolder {
        val bindingItem = DataBindingUtil.inflate<ItemVisaFaqBinding>(
            LayoutInflater.from(parent.context),
            R.layout.item_visa_faq, parent, false
        )
        return VisaFaqViewHolder(bindingItem)
    }

    override fun getItemCount() = faqList.size

    override fun onBindViewHolder(holder: VisaFaqViewHolder, position: Int) {
        val faq = faqList[position]
        holder.bindingView.tvVisaQs.text = faq.question
        holder.bindingView.tvVisaAnswer.text = faq.answer
        if (faq.isExpanded) {
            holder.bindingView.ivChecked.rotation = 0f
            holder.bindingView.constraintExpandable.visibility = View.GONE
        } else {
            holder.bindingView.ivChecked.rotation = 180f
            holder.bindingView.constraintExpandable.visibility = View.VISIBLE
        }
    }

    fun updateData(arrayList: List<VisaFaq>?) {
        faqList.clear()
        faqList.addAll(arrayList ?: return)
        notifyDataSetChanged()
    }

    inner class VisaFaqViewHolder(val bindingView: ItemVisaFaqBinding) :
        RecyclerView.ViewHolder(bindingView.root) {

        init {
            bindingView.top.setOnClickListener {
                val faq = faqList[absoluteAdapterPosition]
                faq.isExpanded = !faq.isExpanded
                notifyItemChanged(absoluteAdapterPosition)
            }
        }
    }
}
