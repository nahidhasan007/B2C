package net.sharetrip.profile.view.list

import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.databinding.DataBindingUtil
import androidx.recyclerview.widget.RecyclerView
import net.sharetrip.profile.R
import net.sharetrip.profile.databinding.ItemCountryCodeBinding
import net.sharetrip.profile.model.CountryCode
import java.util.*

class CountryCurrencyAdapter : RecyclerView.Adapter<CountryCurrencyAdapter.CountryCurrencyViewHolder>() {
    private val countryCodeList = ArrayList<CountryCode>()

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): CountryCurrencyViewHolder {
        val countryView = DataBindingUtil.inflate<ItemCountryCodeBinding>(LayoutInflater.from(parent.context), R.layout.item_country_code, parent, false)
        return CountryCurrencyViewHolder(countryView)
    }

    override fun onBindViewHolder(holder: CountryCurrencyViewHolder, position: Int) {
        holder.bindingView.countryCodeInfo = countryCodeList[position]
    }

    override fun getItemCount() = countryCodeList.size

    fun updateCountryCurrencyList(countryCodeList: List<CountryCode>?) {
        if (countryCodeList != null) {
            this.countryCodeList.clear()
            this.countryCodeList.addAll(countryCodeList)
            notifyDataSetChanged()
        }
    }

    inner class CountryCurrencyViewHolder(val bindingView: ItemCountryCodeBinding) : RecyclerView.ViewHolder(bindingView.root)
}
