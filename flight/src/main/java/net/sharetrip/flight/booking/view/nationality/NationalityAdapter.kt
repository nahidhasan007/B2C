package net.sharetrip.flight.booking.view.nationality

import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.databinding.DataBindingUtil
import androidx.recyclerview.widget.RecyclerView
import net.sharetrip.flight.R
import net.sharetrip.flight.booking.model.NationalityCode
import net.sharetrip.flight.databinding.ItemNationalityCodeFlightBinding
import java.util.*

class NationalityAdapter : RecyclerView.Adapter<NationalityAdapter.NationalityHolder>() {
    private val countryCodeList = ArrayList<NationalityCode>()

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): NationalityHolder {
        val countryView = DataBindingUtil.inflate<ItemNationalityCodeFlightBinding>(
            LayoutInflater.from(parent.context),
            R.layout.item_nationality_code_flight,
            parent,
            false
        )
        return NationalityHolder(countryView)
    }

    override fun onBindViewHolder(holder: NationalityHolder, position: Int) {
        holder.bindingView.countryCodeInfo = countryCodeList[position]
    }

    override fun getItemCount() = countryCodeList.size

    fun updateCodeList(countryCodeList: List<NationalityCode>?) {
        if (countryCodeList != null) {
            this.countryCodeList.clear()
            this.countryCodeList.addAll(countryCodeList)
            notifyDataSetChanged()
        }
    }

    inner class NationalityHolder(val bindingView: ItemNationalityCodeFlightBinding) :
        RecyclerView.ViewHolder(bindingView.root)
}

