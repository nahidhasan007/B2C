package net.sharetrip.flight.history.view.refundselectpassenger

import android.os.Bundle
import androidx.core.os.bundleOf
import androidx.navigation.fragment.findNavController
import com.sharetrip.base.data.PrefKey
import com.sharetrip.base.data.SharedPrefsHelper
import com.sharetrip.base.event.EventObserver
import net.sharetrip.shared.view.BaseFragment
import com.sharetrip.base.viewmodel.BaseViewModel
import net.sharetrip.flight.R
import net.sharetrip.flight.databinding.FragmentFlightRefundVoidPassengerSelectionBinding
import net.sharetrip.flight.history.model.FlightHistoryResponse
import net.sharetrip.flight.history.model.Traveller
import net.sharetrip.flight.history.view.bookingdetails.FlightBookingDetailsFragment.Companion.ARG_HISTORY_DATA
import net.sharetrip.flight.history.view.list.FlightHistoryListFragment.Companion.ARG_FLIGHT_HISTORY_RESPONSE
import net.sharetrip.flight.history.view.refundselectpassenger.adapter.PassengerAdapter
import net.sharetrip.flight.network.DataManager
import net.sharetrip.flight.utils.setTitleSubTitle

class SelectPassengerFragment : BaseFragment<FragmentFlightRefundVoidPassengerSelectionBinding>() {

    private val viewModel: SelectPassengerViewModel by lazy {
        SelectPassengerViewModelFactory(
            DataManager.flightHistoryApiService,
            historyData.bookingCode,
            SharedPrefsHelper(requireContext())[PrefKey.ACCESS_TOKEN, ""]
        ).create(SelectPassengerViewModel::class.java)
    }

    private val historyData: FlightHistoryResponse by lazy {
        requireArguments().getBundle(ARG_HISTORY_DATA)?.getParcelable(
            ARG_FLIGHT_HISTORY_RESPONSE
        )!!
    }

    private val passengerAdapter: PassengerAdapter by lazy {
        PassengerAdapter(listOf(), viewModel::onPassengerSelected)
    }

    override fun layoutId() = R.layout.fragment_flight_refund_void_passenger_selection

    override fun getViewModel(): BaseViewModel =  viewModel

    override fun initOnCreateView() {
        setTitleSubTitle(getString(R.string.select_passenger))

        bindingView.viewModel = viewModel
        bindingView.passengerRecyclerView.adapter = passengerAdapter
        viewModel.passengers.clear()

        viewModel.eligibleTravelers.observe(viewLifecycleOwner, EventObserver {
            if (it.isNotEmpty())
                passengerAdapter.resetItems(it)
        })

        viewModel.selectAllClicked.observe(viewLifecycleOwner, EventObserver{
            passengerAdapter.selectAll()
        })

        viewModel.goToPricingDetails.observe(viewLifecycleOwner, EventObserver {
            val traveller = mutableListOf<Traveller>()
            for (pos in viewModel.passengers)
                traveller.add(viewModel.eligibleTravellerList[pos])

            val data = Bundle()
            data.putParcelableArrayList(ARG_SELECTED_PASSENGERS, traveller as ArrayList<Traveller>)
            data.putParcelable(ARG_REFUND_QUOTATION_RESPONSE, it)

            findNavController().navigate(
                R.id.action_selectPassengerFragment_to_pricingDetailsFragment,
                bundleOf(ARG_REFUND_VOID_DATA to data)
            )
        })
    }

    companion object {
        const val ARG_REFUND_VOID_DATA = "ARG_REFUND_VOID_DATA"
        const val ARG_SELECTED_PASSENGERS = "ARG_SELECTED_PASSENGERS"
        const val ARG_REFUND_QUOTATION_RESPONSE = "ARG_REFUND_QUOTATION_RESPONSE"
    }
}
