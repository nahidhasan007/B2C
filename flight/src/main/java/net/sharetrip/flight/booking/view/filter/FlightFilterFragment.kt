package net.sharetrip.flight.booking.view.filter

import android.annotation.SuppressLint
import android.os.Bundle
import android.view.View.GONE
import androidx.fragment.app.viewModels
import androidx.lifecycle.ViewModelProvider
import androidx.navigation.fragment.findNavController
import com.google.gson.Gson
import net.sharetrip.shared.view.BaseFragment
import com.sharetrip.base.viewmodel.BaseViewModel
import net.sharetrip.flight.R
import net.sharetrip.flight.booking.model.Price
import net.sharetrip.flight.booking.model.filter.FilterParams
import net.sharetrip.flight.booking.model.filter.FlightFilter
import net.sharetrip.flight.databinding.FragmentFlightFilterBinding
import net.sharetrip.flight.utils.MoshiUtil
import net.sharetrip.flight.utils.ShearedViewModel
import java.io.IOException

class FlightFilterFragment : BaseFragment<FragmentFlightFilterBinding>() {
    private val viewModel by viewModels<FlightFilterViewModel>()
    private lateinit var flightFilter: FlightFilter

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        val flightFilterBundle = requireArguments().getBundle(ARG_FLIGHT_FILTER_BUNDLE)
        val flightInfoString = flightFilterBundle?.getString(ARG_FLIGHT_FILTER_INFO_STRING_MODEL)
        val flightCount = flightFilterBundle?.getInt(ARG_FLIGHT_FILTER_INFO_STRING_FLIGHT_COUNT)
        try {
            flightFilter =
                MoshiUtil.convertStringToModelClass(flightInfoString, FlightFilter::class.java)
        } catch (e: IOException) {
            e.printStackTrace()
        }
        viewModel.filterPrice = Price(flightFilter.price.max, flightFilter.price.min)
        viewModel.totalFlight.set("$flightCount Available Flights")
    }

    override fun layoutId(): Int = R.layout.fragment_flight_filter

    override fun getViewModel(): BaseViewModel = viewModel

    @SuppressLint("SetTextI18n")
    override fun initOnCreateView() {
        val shearedViewModel =
            ViewModelProvider(requireActivity()).get(ShearedViewModel::class.java)
        bindingView.viewModel = viewModel
        bindingView.executePendingBindings()

        if (flightFilter.weight.isEmpty()) {
            bindingView.cardViewWeight.visibility = GONE
        }
        viewModel.liveData.observe(viewLifecycleOwner) { enum ->
            val bundle = Bundle()
            bundle.putInt("Title", enum)
            bundle.putString("flightFilter", Gson().toJson(flightFilter))
            val addPhotoBottomDialogFragment: FilterBottomSheet = FilterBottomSheet.newInstance()
            addPhotoBottomDialogFragment.arguments = bundle

            childFragmentManager.let {
                addPhotoBottomDialogFragment.show(
                    it,
                    "FlightFilterFragment"
                )
            }
        }

        viewModel.onApplyClicked.observe(viewLifecycleOwner) {
            val bundle = Bundle()
            bundle.putString(
                ARG_FLIGHT_FILTER_INFO_STRING_MODEL, MoshiUtil.convertModelClassToString(
                    viewModel.filterParams!!, FilterParams::class.java
                )
            )
            findNavController().previousBackStackEntry?.savedStateHandle?.set(
                ARG_FLIGHT_FILTER_BUNDLE,
                bundle
            )
            findNavController().popBackStack()
        }

        viewModel.onClickReset.observe(viewLifecycleOwner) {
            bindingView.priceRangeSummary.text = "Any"
            bindingView.refundable.text = "Any"
            bindingView.stopSummary.text = "Any"
            bindingView.onwardTimeSummary.text = "Any"
            bindingView.airlineSummary.text = "Any"
            bindingView.layoverSummary.text = "Any"
            bindingView.weightSummay.text = "Any"
        }

        shearedViewModel.airlinesCodeSets.observe(viewLifecycleOwner) {
            viewModel.airlineList = it
            if (it.size <= 6)
                bindingView.airlineSummary.text = it.joinToString(",")
            else
                bindingView.airlineSummary.text =
                    it.size.toString() + " " + getString(R.string.selected)
        }

        shearedViewModel.layoverCodeSets.observe(viewLifecycleOwner) {
            viewModel.layoverList = it

            if (it.size == 1)
                bindingView.layoverSummary.text = it.joinToString()
            else if (it.size > 1)
                bindingView.layoverSummary.text =
                    it.size.toString() + " " + getString(R.string.selected)
        }

        shearedViewModel.stopCodeSets.observe(viewLifecycleOwner) {
            viewModel.stopList = it

            if (it.size == 1)
                bindingView.stopSummary.text = it.joinToString()
            else if (it.size > 1)
                bindingView.stopSummary.text =
                    it.size.toString() + " " + getString(R.string.selected)
        }

        shearedViewModel.weightCodeSets.observe(viewLifecycleOwner) {
            viewModel.weightList = it
            bindingView.weightSummay.text = shearedViewModel.weightString
        }

        shearedViewModel.outboundCodeSets.observe(viewLifecycleOwner) {
            viewModel.onwardTimeList = it
        }

        shearedViewModel.returnCodeSets.observe(viewLifecycleOwner) {
            viewModel.returnTimeList = it
        }

        shearedViewModel.onScheduleSelected.observe(viewLifecycleOwner) {
            bindingView.onwardTimeSummary.text = it
        }

        shearedViewModel.newPrice.observe(viewLifecycleOwner) {
            viewModel.filterPrice = it
            bindingView.priceRangeSummary.text = "${it.min} - ${it.max}"
        }

        shearedViewModel.isRefundableCodeSets.observe(viewLifecycleOwner) {
            val refundable: ArrayList<Int> = ArrayList()

            for (refundCodes in it) {
                refundable.add(refundCodes.value)
            }
            viewModel.isRefundableList = refundable

            if (it.size > 1) {
                bindingView.refundable.text = "${it.size} Selected"
            }
            if (it.size == 1) {
                bindingView.refundable.text = it[0].key
            }
            if (it.size == 0) {
                bindingView.refundable.text = getString(R.string.any)
            }
        }
    }

    companion object {
        const val ARG_FLIGHT_FILTER_INFO_STRING_MODEL = "ARG_FLIGHT_FILTER_INFO_STRING_MODEL"
        const val ARG_FLIGHT_FILTER_INFO_STRING_FLIGHT_COUNT =
            "ARG_FLIGHT_FILTER_INFO_STRING_FLIGHT_COUNT"
        const val ARG_FLIGHT_FILTER_BUNDLE = "ARG_FLIGHT_FILTER_BUNDLE"
    }
}
