package net.sharetrip.flight.booking.view.filter

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.appcompat.widget.SwitchCompat
import androidx.databinding.DataBindingUtil
import androidx.recyclerview.widget.RecyclerView
import net.sharetrip.flight.R
import net.sharetrip.flight.booking.model.Price
import net.sharetrip.flight.booking.model.filter.*
import net.sharetrip.flight.databinding.ItemScheduleBinding
import net.sharetrip.flight.utils.ShearedViewModel
import net.sharetrip.shared.databinding.ItemFiltersBinding
import net.sharetrip.shared.databinding.ItemPriceRangeBinding
import java.text.NumberFormat
import java.util.*

class FilterAdapter(private val dataList: List<*>, private val viewModel: ShearedViewModel) :
    RecyclerView.Adapter<RecyclerView.ViewHolder>() {
    private val codeSet: MutableSet<String>
    private val refundCodeSet = ArrayList<Refundable>()
    val outboundCodeList = ArrayList<String>()
    val returnCodeList = ArrayList<String>()
    lateinit var price: Price
    private var isSelectAll = false

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
            TIME -> {
                val binding = DataBindingUtil.inflate<ItemScheduleBinding>(
                    inflater,
                    R.layout.item_schedule,
                    parent,
                    false
                )
                ScheduleViewHolder(binding)
            }
            else -> {
                val binding = DataBindingUtil.inflate<ItemFiltersBinding>(
                    inflater,
                    R.layout.item_filters,
                    parent,
                    false
                )
                StopViewHolder(binding)
            }
        }
        return viewHolder
    }

    override fun onBindViewHolder(holder: RecyclerView.ViewHolder, position: Int) {
        when (holder.itemViewType) {
            PRICE -> {
                onPriceViewHolder(holder as PriceViewHolder, position)
            }
            TIME -> {
                onScheduleViewHolder(holder as ScheduleViewHolder, position)
            }
            else -> {
                for (i in dataList.indices) {
                    if (isSelectAll) {
                        addCode(i)
                    } else {
                        removeCode(position)
                    }
                }
                onCommonViewHolder(holder as StopViewHolder, position)
            }
        }
    }

    private fun onPriceViewHolder(holder: PriceViewHolder, position: Int) {
        price = dataList[position] as Price
        holder.mBinding.textViewMinimumPrice.text = price.min.toString()
        holder.mBinding.textViewMaximumPrice.text = price.max.toString()
        holder.mBinding.priceSeekBar.setMaxValue(price.max)
        holder.mBinding.priceSeekBar.setMinValue(price.min)
        holder.mBinding.priceSeekBar.setOnRangeSeekbarChangeListener { minValue: Number, maxValue: Number ->
            holder.mBinding.textViewMinimumPrice.text =
                NumberFormat.getNumberInstance(Locale.US).format(minValue)
            holder.mBinding.textViewMaximumPrice.text =
                NumberFormat.getNumberInstance(Locale.US).format(maxValue)
            price.max = maxValue.toFloat()
            price.min = minValue.toFloat()
        }
    }

    private fun onScheduleViewHolder(holder: ScheduleViewHolder, position: Int) {
        val schedule = dataList[position] as Schedule
        var count = 0
        for (outbound in schedule.outbound) {
            when (outbound.value) {
                "00 - 06" -> {
                    holder.mBinding.cardMidnightDepart.visibility = View.VISIBLE
                    holder.mBinding.cardMidnightDepart.tag = count
                }
                "06 - 12" -> {
                    holder.mBinding.cardMorningDepart.visibility = View.VISIBLE
                    holder.mBinding.cardMorningDepart.tag = count
                }
                "12 - 18" -> {
                    holder.mBinding.cardNoonDepart.visibility = View.VISIBLE
                    holder.mBinding.cardNoonDepart.tag = count
                }
                "18 - 24" -> {
                    holder.mBinding.cardNightDepart.visibility = View.VISIBLE
                    holder.mBinding.cardNightDepart.tag = count
                }
            }
            count++
        }
        count = 0
        for (aReturn in schedule.get_return()) {
            when (aReturn.value) {
                "00 - 06" -> {
                    holder.mBinding.cardMidNightReturn.visibility = View.VISIBLE
                    holder.mBinding.cardMidNightReturn.tag = count
                }
                "06 - 12" -> {
                    holder.mBinding.cardMorningReturn.visibility = View.VISIBLE
                    holder.mBinding.cardMorningReturn.tag = count
                }
                "12 - 18" -> {
                    holder.mBinding.cardNoonReturn.visibility = View.VISIBLE
                    holder.mBinding.cardNoonReturn.tag = count
                }
                "18 - 24" -> {
                    holder.mBinding.cardNightReturn.visibility = View.VISIBLE
                    holder.mBinding.cardNightReturn.tag = count
                }
            }
            count++
        }
        holder.mBinding.cardMidnightDepart.setOnClickListener {
            outboundCodeList.clear()
            outboundCodeList.add(schedule.outbound[(holder.mBinding.cardMidnightDepart.tag as Int)].key)
            holder.mBinding.imageMidnightDepart.visibility = View.VISIBLE
            holder.mBinding.imageMorningDepart.visibility = View.GONE
            holder.mBinding.imageNoonDepart.visibility = View.GONE
            holder.mBinding.imageNightDepart.visibility = View.GONE
        }
        holder.mBinding.cardMorningDepart.setOnClickListener {
            outboundCodeList.clear()
            outboundCodeList.add(schedule.outbound[(holder.mBinding.cardMorningDepart.tag as Int)].key)
            holder.mBinding.imageMidnightDepart.visibility = View.GONE
            holder.mBinding.imageMorningDepart.visibility = View.VISIBLE
            holder.mBinding.imageNoonDepart.visibility = View.GONE
            holder.mBinding.imageNightDepart.visibility = View.GONE
        }
        holder.mBinding.cardNoonDepart.setOnClickListener {
            outboundCodeList.clear()
            outboundCodeList.add(schedule.outbound[(holder.mBinding.cardNoonDepart.tag as Int)].key)
            holder.mBinding.imageMidnightDepart.visibility = View.GONE
            holder.mBinding.imageMorningDepart.visibility = View.GONE
            holder.mBinding.imageNoonDepart.visibility = View.VISIBLE
            holder.mBinding.imageNightDepart.visibility = View.GONE
        }
        holder.mBinding.cardNightDepart.setOnClickListener {
            outboundCodeList.clear()
            outboundCodeList.add(schedule.outbound[(holder.mBinding.cardNightDepart.tag as Int)].key)
            holder.mBinding.imageMidnightDepart.visibility = View.GONE
            holder.mBinding.imageMorningDepart.visibility = View.GONE
            holder.mBinding.imageNoonDepart.visibility = View.GONE
            holder.mBinding.imageNightDepart.visibility = View.VISIBLE
        }

        if (schedule.get_return().isEmpty()) {
            holder.mBinding.cardReturn.visibility = View.GONE
            holder.mBinding.returnText.visibility = View.GONE
        }

        holder.mBinding.cardMidNightReturn.setOnClickListener {
            returnCodeList.clear()
            returnCodeList.add(schedule.get_return()[(holder.mBinding.cardMidNightReturn.tag as Int)].key)
            holder.mBinding.imageMidnightReturn.visibility = View.VISIBLE
            holder.mBinding.imageMorningReturn.visibility = View.GONE
            holder.mBinding.imageNoonReturn.visibility = View.GONE
            holder.mBinding.imageNightReturn.visibility = View.GONE
        }
        holder.mBinding.cardMorningReturn.setOnClickListener {
            returnCodeList.clear()
            returnCodeList.add(schedule.get_return()[(holder.mBinding.cardMorningReturn.tag as Int)].key)
            holder.mBinding.imageMidnightReturn.visibility = View.GONE
            holder.mBinding.imageMorningReturn.visibility = View.VISIBLE
            holder.mBinding.imageNoonReturn.visibility = View.GONE
            holder.mBinding.imageNightReturn.visibility = View.GONE
        }
        holder.mBinding.cardNoonReturn.setOnClickListener {
            returnCodeList.clear()
            returnCodeList.add(schedule.get_return()[(holder.mBinding.cardNoonReturn.tag as Int)].key)
            holder.mBinding.imageMidnightReturn.visibility = View.GONE
            holder.mBinding.imageMorningReturn.visibility = View.GONE
            holder.mBinding.imageNoonReturn.visibility = View.VISIBLE
            holder.mBinding.imageNightReturn.visibility = View.GONE
        }
        holder.mBinding.cardNightReturn.setOnClickListener {
            returnCodeList.clear()
            returnCodeList.add(schedule.get_return()[(holder.mBinding.cardNightReturn.tag as Int)].key)
            holder.mBinding.imageMidnightReturn.visibility = View.GONE
            holder.mBinding.imageMorningReturn.visibility = View.GONE
            holder.mBinding.imageNoonReturn.visibility = View.GONE
            holder.mBinding.imageNightReturn.visibility = View.VISIBLE
        }
    }

    private fun onCommonViewHolder(holder: StopViewHolder, position: Int) {
        if (dataList[position] is Stop) {
            holder.mBinding.checkboxFilter.text = (dataList[position] as Stop).name
            holder.mBinding.checkboxFilter.tag = (dataList[position] as Stop).id
        }
        if (dataList[position] is Airline) {
            holder.mBinding.checkboxFilter.text = (dataList[position] as Airline).short
            holder.mBinding.checkboxFilter.tag = (dataList[position] as Airline).code
        }
        if (dataList[position] is Layover) {
            holder.mBinding.checkboxFilter.text = (dataList[position] as Layover).name
            holder.mBinding.checkboxFilter.tag = (dataList[position] as Layover).iata
        }
        if (dataList[position] is Weight) {
            holder.mBinding.checkboxFilter.text = (dataList[position] as Weight).note
            holder.mBinding.checkboxFilter.tag = (dataList[position] as Weight).key
        }

        if (dataList[position] is Refundable) {
            holder.mBinding.checkboxFilter.text = (dataList[position] as Refundable).key
            holder.mBinding.checkboxFilter.tag = (dataList[position] as Refundable).value
        }

        holder.mBinding.switchFilter.isChecked = isSelectAll
        holder.mBinding.switchFilter.setOnClickListener { view: View ->
            if ((view as SwitchCompat).isChecked) {
                addCode(position)
            } else {
                removeCode(position)
            }
            viewModel.isSelectAll.setValue(codeSet.size == dataList.size)
        }
    }

    override fun getItemViewType(position: Int): Int {
        return when {
            dataList[position] is Stop -> {
                STOPS
            }
            dataList[position] is Airline -> {
                AIRLINE
            }
            dataList[position] is Layover -> {
                LAYOVER
            }
            dataList[position] is Weight -> {
                WEIGHT
            }
            dataList[position] is Price -> {
                PRICE
            }
            dataList[position] is Schedule -> {
                TIME
            }
            dataList[position] is Refundable -> {
                REFUNDABLE
            }
            else -> {
                -1
            }
        }
    }

    override fun getItemCount(): Int {
        return dataList.size
    }

    inner class StopViewHolder internal constructor(val mBinding: ItemFiltersBinding) :
        RecyclerView.ViewHolder(mBinding.root)

    inner class PriceViewHolder internal constructor(val mBinding: ItemPriceRangeBinding) :
        RecyclerView.ViewHolder(mBinding.root)

    inner class ScheduleViewHolder internal constructor(val mBinding: ItemScheduleBinding) :
        RecyclerView.ViewHolder(mBinding.root)

    private fun addCode(position: Int) {
        when {
            dataList[position] is Stop -> {
                codeSet.add((dataList[position] as Stop).id.toString())
                return
            }
            dataList[position] is Airline -> {
                codeSet.add((dataList[position] as Airline).code)
                return
            }
            dataList[position] is Layover -> {
                codeSet.add((dataList[position] as Layover).iata)
                return
            }
            dataList[position] is Weight -> {
                codeSet.add((dataList[position] as Weight).key.toString())
                weightStrings.add((dataList[position] as Weight).note.toString())
                return
            }
            dataList[position] is Outbound -> {
                codeSet.add((dataList[position] as Outbound).key)
                return
            }
            dataList[position] is Return -> {
                codeSet.add((dataList[position] as Return).key)
                return
            }
            dataList[position] is Refundable -> {
                val refundCode = (dataList[position] as Refundable)
                if (!refundCodeSet.contains(refundCode)) {
                    refundCodeSet.add(refundCode)
                }
                return
            }
        }
    }

    private fun removeCode(position: Int) {
        if (dataList[position] is Stop) {
            codeSet.remove((dataList[position] as Stop).id.toString())
            return
        }
        if (dataList[position] is Airline) {
            codeSet.remove((dataList[position] as Airline).code)
            return
        }
        if (dataList[position] is Layover) {
            codeSet.remove((dataList[position] as Layover).iata)
            return
        }
        if (dataList[position] is Weight) {
            codeSet.remove((dataList[position] as Weight).key.toString())
            weightStrings.remove((dataList[position] as Weight).note.toString())
            return
        }
        if (dataList[position] is Outbound) {
            codeSet.remove((dataList[position] as Outbound).key)
            return
        }
        if (dataList[position] is Return) {
            codeSet.remove((dataList[position] as Return).key)
            return
        }
        if (dataList[position] is Refundable) {
            val refundCode = (dataList[position] as Refundable)
            if (refundCodeSet.contains(refundCode)) {
                refundCodeSet.remove(refundCode)
            }
            return
        }
    }

    val codeList: ArrayList<String>
        get() = ArrayList(codeSet)

    val weightStrings = mutableListOf<String>()

    val refundCodeList: ArrayList<Refundable>
        get() = ArrayList(refundCodeSet)

    val refundable: Boolean
        get() = true

    fun setSelection(isSelectAll: Boolean) {
        this.isSelectAll = isSelectAll
    }

    init {
        codeSet = HashSet()
    }
}