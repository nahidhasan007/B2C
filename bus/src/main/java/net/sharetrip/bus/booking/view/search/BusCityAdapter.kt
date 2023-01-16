package net.sharetrip.bus.booking.view.search

import android.view.LayoutInflater
import android.view.ViewGroup
import android.widget.Filter
import androidx.databinding.DataBindingUtil
import androidx.recyclerview.widget.RecyclerView
import net.sharetrip.shared.view.adapter.BaseFilterRecyclerAdapter
import net.sharetrip.bus.R
import net.sharetrip.bus.booking.model.City
import net.sharetrip.bus.databinding.ItemLocationBinding
import java.util.*

class BusCityAdapter : BaseFilterRecyclerAdapter<City, BusCityAdapter.CityViewHolder?>() {

    private val mFilter: Filter = object : Filter() {
        override fun performFiltering(mConstraint: CharSequence): FilterResults {
            val mSearchText = mConstraint.toString().lowercase(Locale.getDefault())
            val mDataSet = dataSet
            val mFilteredAirports: MutableList<City?> = ArrayList()

            if (mSearchText.isNotEmpty()) {
                for (mCity in mDataSet) {
                    if (mCity.stationCode.lowercase(Locale.getDefault()).contains(mSearchText)
                        || mCity.name.lowercase(Locale.getDefault()).contains(mSearchText)
                        || mCity.name.lowercase(Locale.getDefault()).contains(mSearchText)
                    ) {
                        mFilteredAirports.add(mCity)
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
                    val mFilteredCity = it.values as List<City>
                    setFilterDataSet(mFilteredCity)
                }
            } catch (e: Exception) {
            }
        }
    }

    override fun onCreateViewHolder(mParent: ViewGroup, mViewType: Int): CityViewHolder {
        val mContext = mParent.context
        val inflater = LayoutInflater.from(mContext)
        val mView = DataBindingUtil.inflate<ItemLocationBinding>(
            inflater,
            R.layout.item_location,
            mParent,
            false
        )

        return CityViewHolder(mView)
    }

    override fun onBindViewHolder(mHolder: CityViewHolder, mPosition: Int) {
        val city = getItem(mPosition)
        mHolder.bindingView.city = city
    }

    override fun getFilter(): Filter {
        return mFilter
    }

    inner class CityViewHolder(val bindingView: ItemLocationBinding) :
        RecyclerView.ViewHolder(bindingView.root)
}
