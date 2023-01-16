package net.sharetrip.hotel.booking.view.nationality

import android.annotation.SuppressLint
import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import net.sharetrip.hotel.booking.model.NationalityCode
import net.sharetrip.hotel.databinding.ItemNationalityCodeHotelBinding

class NationalityAdapter : RecyclerView.Adapter<NationalityAdapter.CodeViewHolder>() {
    private val countryCodeList = ArrayList<NationalityCode>()

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): CodeViewHolder {
        val binding = ItemNationalityCodeHotelBinding.inflate(
            LayoutInflater.from(parent.context),
            parent,
            false
        )
        return CodeViewHolder(binding)
    }

    override fun onBindViewHolder(holder: CodeViewHolder, position: Int) {
        holder.bindingView.countryCodeInfo = countryCodeList[position]
    }

    override fun getItemCount() = countryCodeList.size

    @SuppressLint("NotifyDataSetChanged")
    fun updateCodeList(countryCodeList: List<NationalityCode>?) {
        if (countryCodeList != null) {
            this.countryCodeList.clear()
            this.countryCodeList.addAll(countryCodeList)
            notifyDataSetChanged()
        }
    }

    inner class CodeViewHolder(val bindingView: ItemNationalityCodeHotelBinding) :
        RecyclerView.ViewHolder(bindingView.root)
}
