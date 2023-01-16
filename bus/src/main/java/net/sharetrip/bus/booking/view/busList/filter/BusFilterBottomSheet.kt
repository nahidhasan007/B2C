package net.sharetrip.bus.booking.view.busList.filter

import android.content.DialogInterface
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.databinding.DataBindingUtil
import androidx.lifecycle.MutableLiveData
import com.google.android.material.bottomsheet.BottomSheetDialogFragment
import net.sharetrip.bus.R
import net.sharetrip.bus.booking.model.*
import net.sharetrip.bus.databinding.LayoutBusFilterBottomSheetBinding
import timber.log.Timber

class BusFilterBottomSheet(private val viewModel: BusListFilterViewModel) :
    BottomSheetDialogFragment() {

    private lateinit var bindingView: LayoutBusFilterBottomSheetBinding
    private var case = 0

    private fun layoutId() = R.layout.layout_bus_filter_bottom_sheet

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        bindingView = DataBindingUtil.inflate(inflater, layoutId(), container, false)
        return bindingView.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        bindingView.model = viewModel
        val bundle = arguments

        try {
            case = bundle!!.getInt("Title")
            viewModel.filterType.set(case)
            when (case) {
                PRICE_RANGE -> {
                    bindingView.seekBarOfPrice.setOnRangeSeekbarChangeListener { minValue, maxValue ->
                        viewModel.maxPrice.set(maxValue.toInt())
                        viewModel.minPrice.set(minValue.toInt())
                    }
                }
                CLASS_FILTER -> {
                    bindingView.filterRecycler.adapter =
                        BusListFilterAdapter(
                            viewModel.dataListClass,
                            case,
                            viewModel,
                            false
                        )
                }
                SCHEDULE_FILTER -> {

                    bindingView.filterRecycler.adapter =
                        BusListFilterAdapter(
                            viewModel.departureScheduleList,
                            case,
                            viewModel,
                            true
                        )


                    bindingView.arivalFilterRecycler.adapter =
                        BusListFilterAdapter(
                            viewModel.arrivalScheduleList,
                            case,
                            viewModel,
                            false
                        )
                }
            }
        } catch (E: Exception) {
            Toast.makeText(requireContext(), "Dangerous Error", Toast.LENGTH_SHORT).show()
        }

        viewModel.allSelectClass.observe(viewLifecycleOwner) {
            if (!it) {
                viewModel.tempSelected = 0
                viewModel.tempValue.clear()
            }
        }

        viewModel.allSelectSchedule.observe(viewLifecycleOwner) {
            if (!it) {
                viewModel.tempSelected = 0
                viewModel.tempValue.clear()
            }
        }

        bindingView.selectAllTextView.setOnClickListener {
            when (case) {
                CLASS_FILTER -> viewModel.allSelectClass.value = !viewModel.checked.get()
                SCHEDULE_FILTER -> viewModel.allSelectSchedule.value = !viewModel.checked.get()
            }
            viewModel.checked.set(!viewModel.checked.get())
            bindingView.arivalFilterRecycler.adapter?.notifyDataSetChanged()
            bindingView.filterRecycler.adapter?.notifyDataSetChanged()
        }

        bindingView.closeFilterImageView.setOnClickListener {
            dismiss()
            viewModel.tempSelected = 0
            viewModel.tempValue.clear()
        }

        bindingView.applyFilterButton.setOnClickListener {
            dismiss()
            when (case) {
                PRICE_RANGE -> {
                    viewModel.filterDataOld.filterPrice =
                        FilterPrice(viewModel.minPrice.get(), viewModel.maxPrice.get())

                    viewModel.filterPriceRangeValue.value =
                        viewModel.minPrice.get().toString() + "-" + viewModel.maxPrice.get()
                            .toString()
                }

                CLASS_FILTER -> {
                    viewModel.filterDataOld.classType.clear()
                    if (viewModel.tempValue.isNotEmpty())
                        viewModel.tempValue.forEach {
                            if (it !in viewModel.filterDataOld.classType)
                                viewModel.filterDataOld.classType.add(it)
                        }
                    else
                        viewModel.filterDataOld.classType = arrayListOf()
                    if (viewModel.tempSelected > 2)
                        viewModel.tempSelected = 2
                    when {
                        viewModel.tempSelected > 1 -> viewModel.filterClassValue.value =
                            "${viewModel.tempSelected} Selected"
                        viewModel.tempSelected == 1 -> viewModel.filterClassValue.value =
                            viewModel.filterDataOld.classType[0]
                        else -> viewModel.filterClassValue.value = "Any"
                    }
                }

                SCHEDULE_FILTER -> {
                    viewModel.filterDataOld.schedule.clear()
                    Timber.d(viewModel.scheduleValue.toString())

                    if (!viewModel.scheduleValue.isEmpty())
                        viewModel.scheduleValue.forEach {
                            viewModel.filterDataOld.schedule.add(
                                FilterSchedule(
                                    it.from,
                                    it.to,
                                    it.scheduleType
                                )
                            )
                        }
                    else
                        viewModel.filterDataOld.schedule = arrayListOf()

                    if (viewModel.tempSelected > 8)
                        viewModel.tempSelected = 8

                    when {
                        viewModel.tempSelected > 1 -> viewModel.filterScheduleValue.value =
                            "${viewModel.tempSelected} Selected"
                        viewModel.tempSelected == 1 -> viewModel.filterScheduleValue.value =
                            "${viewModel.scheduleValue[0].from} - ${viewModel.scheduleValue[0].to}"
                        else -> viewModel.filterScheduleValue.value = "Any"
                    }

                }
            }
            viewModel.tempSelected = 0
        }
    }

    override fun dismiss() {
        super.dismiss()
        when (case) {
            CLASS_FILTER -> viewModel.allSelectClass = MutableLiveData<Boolean>()
            SCHEDULE_FILTER -> viewModel.allSelectSchedule = MutableLiveData<Boolean>()
        }
        viewModel.checked.set(false)
    }

    override fun onDismiss(dialog: DialogInterface) {
        super.onDismiss(dialog)
        viewModel.checked.set(false)
    }

    companion object {
        fun newInstance(viewModel: BusListFilterViewModel): BusFilterBottomSheet {
            return BusFilterBottomSheet(viewModel)
        }
    }
}
