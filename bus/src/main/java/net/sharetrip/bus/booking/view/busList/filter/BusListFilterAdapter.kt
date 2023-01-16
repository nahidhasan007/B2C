package net.sharetrip.bus.booking.view.busList.filter

import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import net.sharetrip.bus.R
import net.sharetrip.bus.booking.model.CLASS_FILTER
import net.sharetrip.bus.booking.model.FilterSchedule
import net.sharetrip.bus.booking.model.SCHEDULE_FILTER
import net.sharetrip.bus.databinding.ItemBusFiltersBinding

class BusListFilterAdapter(
    private val dataList: List<Any>,
    private val case: Int,
    private var viewModel: BusListFilterViewModel,
    private val scheduleCase: Boolean
) : RecyclerView.Adapter<BusListFilterAdapter.ViewHolder>() {

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
        val lf: LayoutInflater = LayoutInflater.from(parent.context)
        val binding = ItemBusFiltersBinding.inflate(lf, parent, false)
        return ViewHolder(binding)
    }

    override fun onBindViewHolder(holder: ViewHolder, position: Int) {
        holder.itemBinding.model = viewModel

        when (case) {
            CLASS_FILTER -> {
                holder.itemBinding.singleFilterValueTextView.text = dataList[position].toString()

                if (viewModel.allSelectClass.value == true) {
                    viewModel.tempSelected++
                    if ((dataList[position] as String) !in viewModel.tempValue)
                        viewModel.tempValue.add(dataList[position] as String)
                    holder.itemBinding.switchFilter.isChecked = true
                } else if (viewModel.allSelectClass.value == false) {
                    viewModel.tempSelected = 0
                    viewModel.tempValue.remove(dataList[position] as String)
                    holder.itemBinding.switchFilter.isChecked = false
                } else if (viewModel.prevChecked) {
                    if (viewModel.tempValue.size > 0) {
                        viewModel.tempValue.forEach {
                            if (dataList[position].toString() == it) {
                                viewModel.tempSelected++
                                holder.itemBinding.switchFilter.isChecked = true
                            }
                        }
                    }
                }

                viewModel.checked.set(viewModel.tempSelected >= 1)

                holder.itemBinding.switchFilter.setOnClickListener {
                    if (holder.itemBinding.switchFilter.isChecked) {
                        viewModel.tempSelected++
                        viewModel.tempValue.add(dataList[position] as String)
                        viewModel.prevChecked = true
                    }
                    if (!holder.itemBinding.switchFilter.isChecked) {
                        viewModel.tempSelected--
                        viewModel.tempValue.remove(dataList[position] as String)
                    }
                    viewModel.checked.set(viewModel.tempSelected >= 1)
                }
            }

            SCHEDULE_FILTER -> {
                if (dataList[position] is FilterSchedule) {
                    val schedule: FilterSchedule = dataList[position] as FilterSchedule
                    holder.itemBinding.singleFilterValueTextView.text =
                        "${schedule.from} - ${schedule.to}"
                }

                if (viewModel.allSelectSchedule.value == true) {
                    if ((dataList[position] as FilterSchedule) !in viewModel.scheduleValue) {
                        viewModel.tempSelected++
                        viewModel.scheduleValue.add(dataList[position] as FilterSchedule)
                        holder.itemBinding.switchFilter.isChecked = true
                    }
                } else if (viewModel.allSelectSchedule.value == false) {
                    if ((dataList[position] as FilterSchedule) in viewModel.scheduleValue) {
                        viewModel.scheduleValue.remove(dataList[position] as FilterSchedule)
                    }
                    viewModel.tempSelected = 0
                    holder.itemBinding.switchFilter.isChecked = false
                } else if (viewModel.prevChecked && scheduleCase) {
                    if (viewModel.scheduleValue.size > 0) {
                        for (it in viewModel.scheduleValue) {
                            if (holder.itemBinding.singleFilterValueTextView.text == "${it.from} - ${it.to}" && it.scheduleType == "departure") {
                                viewModel.tempSelected++
                                holder.itemBinding.switchFilter.isChecked = true
                                break
                            }
                        }
                    }
                } else if (viewModel.prevChecked && !scheduleCase) {
                    if (viewModel.scheduleValue.size > 0) {
                        for (it in viewModel.scheduleValue) {
                            if (holder.itemBinding.singleFilterValueTextView.text == "${it.from} - ${it.to}" && it.scheduleType == "arrival") {
                                viewModel.tempSelected++
                                holder.itemBinding.switchFilter.isChecked = true
                                break
                            }
                        }
                    }
                }

                viewModel.checked.set(viewModel.tempSelected >= 1)

                holder.itemBinding.switchFilter.setOnClickListener {
                    if (holder.itemBinding.switchFilter.isChecked) {
                        if (dataList[position] !in viewModel.scheduleValue)
                            viewModel.scheduleValue.add(dataList[position] as FilterSchedule)
                        viewModel.tempSelected++
                        viewModel.prevChecked = true
                    }
                    if (!holder.itemBinding.switchFilter.isChecked) {
                        viewModel.tempSelected--
                        viewModel.scheduleValue.remove(dataList[position] as FilterSchedule)
                    }
                    if (viewModel.tempSelected > 8)
                        viewModel.tempSelected = 8
                    viewModel.checked.set(viewModel.tempSelected >= 1)
                }

                when (position) {
                    1 -> {
                        holder.itemBinding.sunTimeImageView.setImageResource(R.drawable.ic_time_6_12_mono)
                    }
                    2 -> {
                        holder.itemBinding.sunTimeImageView.setImageResource(R.drawable.ic_time_12_18_mono)
                    }
                    3 -> {
                        holder.itemBinding.sunTimeImageView.setImageResource(R.drawable.ic_time_18_24_mono)
                    }
                    0 -> {
                        holder.itemBinding.sunTimeImageView.setImageResource(R.drawable.ic_time_0_6_mono)
                    }
                }

                if (viewModel.tempSelected > 8)
                    viewModel.tempSelected = 8
            }
        }
    }

    override fun getItemCount(): Int = dataList.size

    inner class ViewHolder(val itemBinding: ItemBusFiltersBinding) :
        RecyclerView.ViewHolder(itemBinding.root)
}
