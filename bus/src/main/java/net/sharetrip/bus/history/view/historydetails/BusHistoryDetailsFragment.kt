package net.sharetrip.bus.history.view.historydetails

import android.graphics.Color
import android.view.View
import android.widget.TextView
import androidx.core.os.bundleOf
import androidx.lifecycle.ViewModelProvider
import androidx.navigation.fragment.findNavController
import net.sharetrip.shared.view.BaseFragment
import com.sharetrip.base.viewmodel.BaseViewModel
import net.sharetrip.bus.R
import net.sharetrip.bus.databinding.FragmentBusHistoryDetailsBinding
import net.sharetrip.bus.history.model.HistoryDetails
import net.sharetrip.bus.history.view.BusHistoryActivity
import net.sharetrip.bus.history.view.historylist.BusHistoryListAdapter
import net.sharetrip.bus.history.view.passengerdetails.BusHistoryPassengerDetailFragment.Companion.ARG_BUS_PASSENGER_DETAILS
import net.sharetrip.bus.history.view.pricingdetails.BusHistoryPricingDetailFragment.Companion.ARG_BUS_PRICING_DETAILS

class BusHistoryDetailsFragment : BaseFragment<FragmentBusHistoryDetailsBinding>() {
    private val viewModel by lazy {
        ViewModelProvider(
            this,
            BusHistoryDetailsVMFactory(historyDetails)
        ).get(
            BusHistoryDetailsViewModel::class.java
        )
    }

    private val historyDetails by lazy {
        requireArguments().getParcelable<HistoryDetails>(
            ARG_BUS_HISTORY_DETAILS
        )!!
    }

    override fun layoutId(): Int = R.layout.fragment_bus_history_details

    override fun getViewModel(): BaseViewModel = viewModel

    override fun initOnCreateView() {
        bindingView.model = viewModel
        bindingView.lifecycleOwner = viewLifecycleOwner
        showTripCoin()
        bindingView.status = BusHistoryListAdapter.getStatus(historyDetails.bookingStatus)
        bindingView.statusColor =
            Color.parseColor(BusHistoryListAdapter.getColor(historyDetails.bookingStatus))

        viewModel.navigateToPricingDetails.observe(viewLifecycleOwner) {
            hideTripCoin()
            findNavController().navigate(
                R.id.action_busHistoryDetailsFragment_to_busHistoryPricingDetailFragment,
                bundleOf(ARG_BUS_PRICING_DETAILS to viewModel.busHistoryData)
            )
        }

        viewModel.navigateToPassengerDetails.observe(viewLifecycleOwner) {
            hideTripCoin()
            findNavController().navigate(
                R.id.action_busHistoryDetailsFragment_to_busHistoryPassengerDetailFragment,
                bundleOf(ARG_BUS_PASSENGER_DETAILS to viewModel.busHistoryData)
            )
        }

        viewModel.navigateToPolicies.observe(viewLifecycleOwner) {
            hideTripCoin()
            findNavController().navigate(
                R.id.action_busHistoryDetailsFragment_to_busHistoryPoliciesFragment
            )
        }
    }

    private fun hideTripCoin() {
        (activity as BusHistoryActivity).findViewById<TextView>(R.id.text_view_trip_coin).visibility =
            View.GONE
    }

    private fun showTripCoin() {
        (activity as BusHistoryActivity).findViewById<TextView>(R.id.text_view_trip_coin).visibility =
            View.VISIBLE
    }

    companion object {
        const val ARG_BUS_HISTORY_DETAILS = "ARG_BUS_HISTORY_DETAILS"
    }
}
