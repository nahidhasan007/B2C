package net.sharetrip.flight.booking.view.searchairport

import android.view.LayoutInflater
import android.view.ViewGroup
import android.widget.Filter
import androidx.databinding.DataBindingUtil
import androidx.recyclerview.widget.RecyclerView
import net.sharetrip.shared.view.adapter.BaseFilterRecyclerAdapter
import net.sharetrip.flight.R
import net.sharetrip.flight.booking.view.searchairport.AirportAdapter.AirportViewHolder
import net.sharetrip.flight.databinding.ItemAirportBinding
import net.sharetrip.flight.booking.view.searchairport.data.Airport
import java.util.*

class AirportAdapter : BaseFilterRecyclerAdapter<Airport, AirportViewHolder?>() {

    private val mFilter: Filter = object : Filter() {
        override fun performFiltering(mConstraint: CharSequence): FilterResults {
            val mSearchText = mConstraint.toString().lowercase(Locale.getDefault())
            val mDataSet = dataSet
            val mFilteredAirports: MutableList<Airport?> = ArrayList()
            if (mSearchText.isNotEmpty()) {
                for (mAirport in mDataSet) {
                    if (mAirport.iata.lowercase(Locale.getDefault()).contains(mSearchText)
                            || mAirport.city.lowercase(Locale.getDefault()).contains(mSearchText)
                            || mAirport.name.lowercase(Locale.getDefault()).contains(mSearchText)) {
                        mFilteredAirports.add(mAirport)
                    }
                }
            }
            val mResults = FilterResults()
            mResults.values = mFilteredAirports
            return mResults
        }

        override fun publishResults(constraint: CharSequence?, mResults: FilterResults?) {
            try {
                mResults?.let {
                    val mFilteredAirports = it.values as List<Airport>
                    setFilterDataSet(mFilteredAirports)
                }
            } catch (e: Exception) {
            }
        }
    }
    override fun onCreateViewHolder(mParent: ViewGroup, mViewType: Int): AirportViewHolder {
        val mContext = mParent.context
        val inflater = LayoutInflater.from(mContext)
        val mView = DataBindingUtil.inflate<ItemAirportBinding>(inflater, R.layout.item_airport, mParent, false)
        return AirportViewHolder(mView)
    }

    override fun onBindViewHolder(mHolder: AirportViewHolder, mPosition: Int) {
        val mAirport = getItem(mPosition)
        mHolder.bindingView.codeTextView.text = mAirport.iata
        mHolder.bindingView.addressTextView.text = mAirport.name
    }

    override fun getFilter(): Filter {
        return mFilter
    }

    inner class AirportViewHolder(val bindingView: ItemAirportBinding) : RecyclerView.ViewHolder(bindingView.root)
}
