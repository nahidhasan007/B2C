package net.sharetrip.flight.history.view.flightdetails

import android.os.Bundle
import com.google.gson.Gson
import net.sharetrip.shared.view.BaseFragment
import com.sharetrip.base.viewmodel.BaseViewModel
import net.sharetrip.flight.R
import net.sharetrip.flight.databinding.FragmentBookingFlightHistoryBinding
import net.sharetrip.flight.history.FlightHistoryActivity
import net.sharetrip.flight.history.model.FlightHistoryResponse
import net.sharetrip.flight.history.view.flightdetails.adapter.FlightDetailsHistoryAdapter
import net.sharetrip.flight.history.view.list.FlightHistoryListFragment.Companion.ARG_FLIGHT_HISTORY_RESPONSE

class FlightDetailsHistoryFragment : BaseFragment<FragmentBookingFlightHistoryBinding>() {

    private lateinit var mFlightSegmentAdapter: FlightDetailsHistoryAdapter
    private lateinit var viewModel: FlightDetailsHistoryViewModel
    private lateinit var historyResponse: FlightHistoryResponse

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        val gson = Gson()
        historyResponse = gson.fromJson(
            requireArguments().getBundle(ARG_FLIGHT_HISTORY_RESPONSE)
                ?.getString(ARG_FLIGHT_HISTORY_RESPONSE), FlightHistoryResponse::class.java
        )
    }

    override fun layoutId(): Int = R.layout.fragment_booking_flight_history

    override fun getViewModel(): BaseViewModel = viewModel

    override fun initOnCreateView() {
        setTitle(getString(R.string.flight_details))

        viewModel = FlightHistoryDetailsViewModelFactory(historyResponse).create(
            FlightDetailsHistoryViewModel::class.java
        )

        mFlightSegmentAdapter = FlightDetailsHistoryAdapter()
        bindingView.bookingDetailsRecycler.adapter = mFlightSegmentAdapter

        viewModel.dataSet.observe(viewLifecycleOwner) {
            mFlightSegmentAdapter.resetItems(it as List<Any?>)
        }
    }

    private fun setTitle(title: String) {
        (activity as FlightHistoryActivity).supportActionBar?.title = title
    }
}
