package net.sharetrip.hotel.booking.view.hotelfilter

import android.annotation.SuppressLint
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.SeekBar
import androidx.appcompat.widget.SwitchCompat
import androidx.core.widget.doOnTextChanged
import androidx.databinding.DataBindingUtil
import androidx.recyclerview.widget.RecyclerView
import com.sharetrip.base.event.Event
import net.sharetrip.hotel.R
import net.sharetrip.hotel.booking.model.*
import net.sharetrip.hotel.databinding.ItemHotelSearchBinding
import net.sharetrip.hotel.databinding.ItemLocationRangeBinding
import net.sharetrip.hotel.databinding.ItemStarFiltersBinding
import net.sharetrip.hotel.utils.ShearedViewModel
import net.sharetrip.shared.databinding.ItemFiltersBinding
import net.sharetrip.shared.databinding.ItemPriceRangeBinding
import java.text.NumberFormat
import java.util.*

class HotelFilterAdapter(
    private val dataList: List<*>,
    private val shearedViewModel: ShearedViewModel
) : RecyclerView.Adapter<RecyclerView.ViewHolder>() {
    private val codeSet: MutableSet<String>
    private val pointOfInterestNameList: MutableSet<String>
    private var low: Long = 0
    private var high: Long = 0
    private var isSelectAll = false
    lateinit var priceRange: PriceRangeDumy
    var hotelName: String = ""
    var locationRange: String = "0"

    init {
        codeSet = HashSet()
        pointOfInterestNameList = HashSet()
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): RecyclerView.ViewHolder {
        val viewHolder: RecyclerView.ViewHolder
        val inflater = LayoutInflater.from(parent.context)
        viewHolder = when (viewType) {
            PRICE -> {
                val binding = DataBindingUtil.inflate<ItemPriceRangeBinding>(
                    inflater,
                    R.layout.item_price_range,
                    parent,
                    false
                )
                PriceViewHolder(binding)
            }
            SEARCH -> {
                val binding = DataBindingUtil.inflate<ItemHotelSearchBinding>(
                    inflater,
                    R.layout.item_hotel_search,
                    parent,
                    false
                )
                HotelSearchViewHolder(binding)
            }
            PROPERTY_RATING -> {
                val binding = DataBindingUtil.inflate<ItemStarFiltersBinding>(
                    inflater,
                    R.layout.item_star_filters,
                    parent,
                    false
                )
                StarRatingViewHolder(binding)
            }
            LOCATION_RANGE -> {
                val binding = DataBindingUtil.inflate<ItemLocationRangeBinding>(
                    inflater,
                    R.layout.item_location_range,
                    parent,
                    false
                )
                LocationRangeViewHolder(binding)
            }
            else -> {
                val binding = DataBindingUtil.inflate<ItemFiltersBinding>(
                    inflater,
                    R.layout.item_filters,
                    parent,
                    false
                )
                CommonViewHolder(binding)
            }
        }
        return viewHolder
    }

    override fun onBindViewHolder(holder: RecyclerView.ViewHolder, position: Int) {
        when (holder.itemViewType) {
            PRICE -> {
                onPriceViewHolder(holder as PriceViewHolder, position)
            }
            SEARCH -> {
                onHotelSearchViewHolder(holder as HotelSearchViewHolder, position)
            }
            LOCATION_RANGE -> {
                onLocationRangeViewHolder(holder as LocationRangeViewHolder, position)
            }
            PROPERTY_RATING -> {
                onStarRatingViewHolder(holder as StarRatingViewHolder, position)
            }
            else -> {
                onCommonViewHolder(holder as CommonViewHolder, position)
            }
        }
    }

    private fun onHotelSearchViewHolder(holder: HotelSearchViewHolder, position: Int) {
        holder.mBinding.editTextHotel.setText((dataList[position] as SearchHotel).name)
        holder.mBinding.editTextHotel.doOnTextChanged { text, _, _, _ ->
            hotelName = text.toString().trim()
        }
    }

    @SuppressLint("SetTextI18n")
    private fun onLocationRangeViewHolder(holder: LocationRangeViewHolder, position: Int) {
        holder.mBinding.seekBar.max = (dataList[position] as LocationRange).range
        holder.mBinding.seekBar.progress = (dataList[position] as LocationRange).progress
        locationRange = "" + (dataList[position] as LocationRange).progress
        holder.mBinding.textDistance.text =
            "" + (dataList[position] as LocationRange).progress + " KM"
        holder.mBinding.seekBar.setOnSeekBarChangeListener(object :
            SeekBar.OnSeekBarChangeListener {
            override fun onProgressChanged(seekBar: SeekBar, progress: Int, fromUser: Boolean) {
                holder.mBinding.textDistance.text = "$progress KM"
                locationRange = "" + progress
            }

            override fun onStartTrackingTouch(seekBar: SeekBar) {
            }

            override fun onStopTrackingTouch(seekBar: SeekBar) {
            }
        })
    }

    @SuppressLint("SetTextI18n")
    private fun onPriceViewHolder(holder: PriceViewHolder, position: Int) {
        priceRange = dataList[position] as PriceRangeDumy
        holder.mBinding.textViewMinimumPrice.text = "" + priceRange.low
        holder.mBinding.textViewMaximumPrice.text = "" + priceRange.high
        holder.mBinding.priceSeekBar.setMaxValue(priceRange.high.toFloat())
        holder.mBinding.priceSeekBar.setMinValue(priceRange.low.toFloat())
        holder.mBinding.priceSeekBar.setOnRangeSeekbarChangeListener { minValue: Number, maxValue: Number ->
            holder.mBinding.textViewMinimumPrice.text =
                NumberFormat.getNumberInstance(Locale.US).format(minValue)
            holder.mBinding.textViewMaximumPrice.text =
                NumberFormat.getNumberInstance(Locale.US).format(maxValue)
            high = maxValue as Long
            low = minValue as Long
        }
        holder.mBinding.priceSeekBar.setMinStartValue(priceRange.lowProgress.toFloat())
        holder.mBinding.priceSeekBar.setMaxStartValue(priceRange.highProgress.toFloat())
        holder.mBinding.priceSeekBar.apply()
    }

    @SuppressLint("InflateParams")
    private fun onStarRatingViewHolder(holder: StarRatingViewHolder, position: Int) {
        holder.mBinding.starLayout.removeAllViews()
        val inflater = LayoutInflater.from(holder.mBinding.starLayout.context)

        for (i in 1..(dataList[position] as PropertyRating).rating) {
            val item: View = inflater.inflate(R.layout.star_layout, null, true)
            holder.mBinding.starLayout.addView(item)
        }
        val isSelected = (dataList[position] as PropertyRating).isSelected || isSelectAll
        holder.mBinding.switchFilter.isChecked = isSelected

        if (isSelected) {
            addCode(position)
        } else {
            removeCode(position)
        }

        holder.mBinding.switchFilter.setOnClickListener { view: View ->
            if ((view as SwitchCompat).isChecked) {
                addCode(position)
            } else {
                removeCode(position)
            }
        }
    }

    @SuppressLint("SetTextI18n")
    private fun onCommonViewHolder(holder: CommonViewHolder, position: Int) {
        var isSelected = false
        if (dataList[position] is Neighborhood) {
            holder.mBinding.checkboxFilter.text =
                (dataList[position] as Neighborhood).name + " (" + (dataList[position] as Neighborhood).hotelsCount + ")"
            holder.mBinding.checkboxFilter.tag = (dataList[position] as Neighborhood).id
            isSelected = (dataList[position] as Neighborhood).isSelected || isSelectAll
        }

        if (dataList[position] is Meals) {
            holder.mBinding.checkboxFilter.text = (dataList[position] as Meals).name
            holder.mBinding.checkboxFilter.tag = (dataList[position] as Meals).id
            isSelected = (dataList[position] as Meals).isSelected || isSelectAll
        }

        if (dataList[position] is PropertyType) {
            holder.mBinding.checkboxFilter.text = (dataList[position] as PropertyType).name
            holder.mBinding.checkboxFilter.tag = (dataList[position] as PropertyType).id
            isSelected = (dataList[position] as PropertyType).isSelected || isSelectAll
        }

        if (dataList[position] is AmenityFilter) {
            holder.mBinding.checkboxFilter.text = (dataList[position] as AmenityFilter).name
            holder.mBinding.checkboxFilter.tag = (dataList[position] as AmenityFilter).id
            isSelected = (dataList[position] as AmenityFilter).isSelected || isSelectAll
        }

        if (dataList[position] is PointOfInterest) {
            holder.mBinding.checkboxFilter.text =
                (dataList[position] as PointOfInterest).name + " (" + (dataList[position] as PointOfInterest).hotelsCount + ")"
            holder.mBinding.checkboxFilter.tag = (dataList[position] as PointOfInterest).id
            isSelected = (dataList[position] as PointOfInterest).isSelected || isSelectAll
        }
        holder.mBinding.switchFilter.isChecked = isSelected

        if (isSelected) {
            addCode(position)
        } else {
            removeCode(position)
        }

        holder.mBinding.switchFilter.setOnClickListener { view: View ->
            if ((view as SwitchCompat).isChecked) {
                addCode(position)
            } else {
                removeCode(position)
            }
            shearedViewModel.isSelectAll.setValue(Event(codeSet.size == dataList.size))
        }
    }

    override fun getItemViewType(position: Int): Int {
        return when {
            dataList[position] is PriceRangeDumy -> {
                PRICE
            }
            dataList[position] is SearchHotel -> {
                SEARCH
            }
            dataList[position] is Neighborhood -> {
                NEIGHBORHOOD
            }
            dataList[position] is LocationRange -> {
                LOCATION_RANGE
            }
            dataList[position] is PropertyRating -> {
                PROPERTY_RATING
            }
            dataList[position] is Meals -> {
                MEAL
            }
            dataList[position] is PropertyType -> {
                PROPERTY_TYPE
            }
            dataList[position] is AmenityFilter -> {
                FACILITIES
            }
            dataList[position] is PointOfInterest -> {
                POINT_OF_INTEREST
            }
            else -> {
                -1
            }
        }
    }

    override fun getItemCount(): Int {
        return dataList.size
    }

    inner class CommonViewHolder internal constructor(val mBinding: ItemFiltersBinding) :
        RecyclerView.ViewHolder(mBinding.root)

    inner class PriceViewHolder internal constructor(val mBinding: ItemPriceRangeBinding) :
        RecyclerView.ViewHolder(mBinding.root)

    inner class HotelSearchViewHolder internal constructor(val mBinding: ItemHotelSearchBinding) :
        RecyclerView.ViewHolder(mBinding.root)

    inner class StarRatingViewHolder internal constructor(val mBinding: ItemStarFiltersBinding) :
        RecyclerView.ViewHolder(mBinding.root)

    inner class LocationRangeViewHolder internal constructor(val mBinding: ItemLocationRangeBinding) :
        RecyclerView.ViewHolder(mBinding.root)


    private fun addCode(position: Int) {
        if (dataList[position] is Neighborhood) {
            codeSet.add((dataList[position] as Neighborhood).id.toString())
            return
        }

        if (dataList[position] is Meals) {
            codeSet.add((dataList[position] as Meals).id)
            return
        }

        if (dataList[position] is PropertyType) {
            codeSet.add((dataList[position] as PropertyType).id)
            return
        }

        if (dataList[position] is AmenityFilter) {
            codeSet.add((dataList[position] as AmenityFilter).id)
            return
        }

        if (dataList[position] is PointOfInterest) {
            codeSet.add((dataList[position] as PointOfInterest).id)
            (dataList[position] as PointOfInterest).name?.let {
                pointOfInterestNameList.add(it)
            }
            return
        }

        if (dataList[position] is PropertyRating) {
            codeSet.add((dataList[position] as PropertyRating).rating.toString())
            return
        }
    }

    private fun removeCode(position: Int) {
        if (dataList[position] is Neighborhood) {
            codeSet.remove((dataList[position] as Neighborhood).id)
            return
        }

        if (dataList[position] is Meals) {
            codeSet.remove((dataList[position] as Meals).id)
            return
        }

        if (dataList[position] is PropertyType) {
            codeSet.remove((dataList[position] as PropertyType).id)
            return
        }

        if (dataList[position] is AmenityFilter) {
            codeSet.remove((dataList[position] as AmenityFilter).id)
            return
        }

        if (dataList[position] is PointOfInterest) {
            codeSet.remove((dataList[position] as PointOfInterest).id)
            pointOfInterestNameList.remove((dataList[position] as PointOfInterest).name)
            return
        }

        if (dataList[position] is PropertyRating) {
            codeSet.remove((dataList[position] as PropertyRating).rating.toString())
            return
        }
    }

    fun priceRangeDummyData(): PriceRangeDumy {
        priceRange.highProgress = high
        priceRange.lowProgress = low
        return priceRange
    }

    val codeList: ArrayList<String>
        get() = ArrayList(codeSet)

    val pointOfInterestName: ArrayList<String>
        get() = ArrayList(pointOfInterestNameList)

    fun setSelection(isSelectAll: Boolean, filterType: Int? = null) {
        this.isSelectAll = isSelectAll
        if (!isSelectAll && filterType != null) {
            when (filterType) {
                NEIGHBORHOOD -> {
                    (dataList as List<Neighborhood>).forEach {
                        it.isSelected = false
                    }
                }
                PROPERTY_RATING -> {
                    (dataList as List<PropertyRating>).forEach {
                        it.isSelected = false
                    }
                }
                PROPERTY_TYPE -> {
                    (dataList as List<PropertyType>).forEach {
                        it.isSelected = false
                    }
                }
                FACILITIES -> {
                    (dataList as List<AmenityFilter>).forEach {
                        it.isSelected = false
                    }
                }
                POINT_OF_INTEREST -> {
                    (dataList as List<PointOfInterest>).forEach {
                        it.isSelected = false
                    }
                }
                else -> {}
            }
        }
    }
}
