package net.sharetrip.flight.booking.view.filter

import android.annotation.SuppressLint
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.databinding.DataBindingUtil
import androidx.lifecycle.ViewModelProvider
import com.google.android.material.bottomsheet.BottomSheetBehavior
import com.google.android.material.bottomsheet.BottomSheetDialog
import com.google.android.material.bottomsheet.BottomSheetDialogFragment
import com.google.gson.Gson
import net.sharetrip.flight.R
import net.sharetrip.flight.booking.model.Price
import net.sharetrip.flight.booking.model.filter.FlightFilter
import net.sharetrip.flight.booking.model.filter.Schedule
import net.sharetrip.flight.utils.ShearedViewModel
import net.sharetrip.shared.databinding.LayoutFilterBottomSheetBinding

class FilterBottomSheet : BottomSheetDialogFragment() {
    private lateinit var bindingView: LayoutFilterBottomSheetBinding
    private var enum: Int = 0
    private lateinit var flightFilter: FlightFilter
    private lateinit var viewModel: ShearedViewModel
    lateinit var filterAdapter: FilterAdapter

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        bindingView = DataBindingUtil.inflate(inflater, layoutId(), container, false)

        return bindingView.root
    }

    @SuppressLint("NotifyDataSetChanged")
    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        bindingView.textSelection.text = getString(R.string.select_all)
        viewModel = ViewModelProvider(requireActivity())[ShearedViewModel::class.java]

        (dialog as? BottomSheetDialog)?.behavior?.apply {
            isFitToContents = true
            state = BottomSheetBehavior.STATE_EXPANDED
        }

        val bundle = arguments

        try {
            enum = bundle!!.getInt("Title")
            flightFilter =
                Gson().fromJson(bundle.getString("flightFilter"), FlightFilter::class.java)
            when (enum) {
                PRICE -> {
                    bindingView.textSelection.visibility = View.GONE
                    bindingView.title.text = getString(R.string.price_range)
                    val priceList = ArrayList<Price>()
                    priceList.add(flightFilter.price)
                    filterAdapter = FilterAdapter(priceList as List<Any>, viewModel)
                    bindingView.filterRecycler.adapter = filterAdapter
                }
                STOPS -> {
                    bindingView.textSelection.visibility = View.VISIBLE
                    bindingView.title.text = getString(R.string.stops)
                    filterAdapter = FilterAdapter(flightFilter.stop, viewModel)
                    bindingView.filterRecycler.adapter = filterAdapter

                }
                TIME -> {
                    bindingView.textSelection.visibility = View.GONE
                    val scheduleList = ArrayList<Schedule>()
                    val obj = Schedule(
                        outbound = flightFilter.outbound,
                        _return = flightFilter.get_return()
                    )
                    scheduleList.add(obj)
                    bindingView.title.text = getString(R.string.flight_schedule)
                    filterAdapter = FilterAdapter(scheduleList as List<Any>, viewModel)
                    bindingView.filterRecycler.adapter = filterAdapter
                }
                AIRLINE -> {
                    bindingView.textSelection.visibility = View.VISIBLE
                    bindingView.title.text = getString(R.string.airlines)
                    filterAdapter = FilterAdapter(flightFilter.airlines, viewModel)
                    bindingView.filterRecycler.adapter = filterAdapter
                }
                LAYOVER -> {
                    bindingView.textSelection.visibility = View.VISIBLE
                    bindingView.title.text = getString(R.string.layover)
                    filterAdapter = FilterAdapter(flightFilter.layover, viewModel)
                    bindingView.filterRecycler.adapter = filterAdapter
                }
                WEIGHT -> {
                    bindingView.textSelection.visibility = View.VISIBLE
                    bindingView.title.text = getString(R.string.weight)
                    filterAdapter = FilterAdapter(flightFilter.weight, viewModel)
                    bindingView.filterRecycler.adapter = filterAdapter
                }
                REFUNDABLE -> {
                    bindingView.textSelection.visibility = View.VISIBLE
                    bindingView.title.text = getString(R.string.refundable)
                    filterAdapter = FilterAdapter(flightFilter.isRefundable, viewModel)
                    bindingView.filterRecycler.adapter = filterAdapter
                }
            }
        } catch (e: Exception) {
        }

        bindingView.imageviewClose.setOnClickListener {
            dismiss()
        }

        bindingView.applyButton.setOnClickListener {
            when (enum) {
                PRICE -> viewModel.newPrice.value = filterAdapter.price
                STOPS -> viewModel.stopCodeSets.value = filterAdapter.codeList
                TIME -> {
                    viewModel.outboundCodeSets.value = filterAdapter.outboundCodeList
                    viewModel.returnCodeSets.value = filterAdapter.returnCodeList

                    if (filterAdapter.outboundCodeList.size == 1 && filterAdapter.returnCodeList.size == 0)
                        viewModel.onScheduleSelected.value =
                            filterAdapter.outboundCodeList.joinToString()
                    else if (filterAdapter.outboundCodeList.size == 0 && filterAdapter.returnCodeList.size == 1)
                        viewModel.onScheduleSelected.value =
                            filterAdapter.returnCodeList.joinToString()
                    else
                        viewModel.onScheduleSelected.value = "2 Selected"
                }
                AIRLINE -> viewModel.airlinesCodeSets.value = filterAdapter.codeList
                LAYOVER -> viewModel.layoverCodeSets.value = filterAdapter.codeList
                WEIGHT -> {
                    if (filterAdapter.codeList.size == 1)
                        viewModel.weightString = filterAdapter.weightStrings.joinToString()
                    else
                        viewModel.weightString =
                            filterAdapter.codeList.size.toString() + " Selected"

                    viewModel.weightCodeSets.value = filterAdapter.codeList
                }
                REFUNDABLE -> viewModel.isRefundableCodeSets.value = filterAdapter.refundCodeList
            }
            dismiss()
        }

        viewModel.isSelectAll.observe(viewLifecycleOwner) {
            bindingView.textSelection.text = if (it) "Deselect all" else "Select all"
        }

        bindingView.textSelection.setOnClickListener {
            if (bindingView.textSelection.text.toString().equals("Select all", true)) {
                bindingView.textSelection.text = getString(R.string.deselect_all)
                filterAdapter.setSelection(true)
            } else {
                bindingView.textSelection.text = getString(R.string.select_all)
                filterAdapter.setSelection(false)
            }

            filterAdapter.notifyDataSetChanged()
        }

    }

    private fun layoutId() = R.layout.layout_filter_bottom_sheet

    companion object {
        fun newInstance(): FilterBottomSheet {
            return FilterBottomSheet()
        }
    }
}