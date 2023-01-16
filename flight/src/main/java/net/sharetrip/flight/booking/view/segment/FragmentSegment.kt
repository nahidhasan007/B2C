package net.sharetrip.flight.booking.view.segment

import androidx.navigation.fragment.findNavController
import net.sharetrip.shared.view.BaseFragment
import com.sharetrip.base.viewmodel.BaseViewModel
import net.sharetrip.flight.R
import net.sharetrip.flight.booking.view.segment.adapter.FlightSegmentAdapter
import net.sharetrip.flight.databinding.FragmentSegmentLayoutBinding
import net.sharetrip.flight.booking.model.flightresponse.flights.Flights

class FragmentSegment : BaseFragment<FragmentSegmentLayoutBinding>() {
    private lateinit var mFlights: Flights
    private lateinit var mFlightSegmentAdapter: FlightSegmentAdapter
    private var scrollToPosition: Int = 0

    override fun layoutId() = R.layout.fragment_segment_layout

    override fun getViewModel(): BaseViewModel? = null

    override fun initOnCreateView() {
        mFlights = requireArguments().getBundle(ARA_ITEM_FLIGHTS_SEGMENT_DATA)?.getParcelable(ARA_ITEM_FLIGHTS_SEGMENT)!!
        scrollToPosition = requireArguments().getBundle(ARA_ITEM_FLIGHTS_SEGMENT_DATA)?.getInt(ARA_ITEM_FLIGHTS_SEGMENT_POSITION)!!
        initUi()
    }

    private fun initUi() {
        mFlightSegmentAdapter = FlightSegmentAdapter()
        bindingView.flightSegmentRecyclerView.adapter = mFlightSegmentAdapter
        initDetailsAdapter()
        mFlightSegmentAdapter.onBackPressed().observeForever { findNavController().navigateUp() }
    }

    private fun initDetailsAdapter() {
        val mList = ArrayList<Any>()
        val mFlight = mFlights.flight
        val mItemSegments = mFlights.segments
        if (mFlight.size == mItemSegments.size) {
            val mCount = mFlight.size
            for (i in 0 until mCount) {
                val flight = mFlight[i]
                val mSegments = mItemSegments[i]
                mList.add(flight)
                val segments = mSegments.segment
                segments[segments.size - 1].transitTime = null
                mList.addAll(segments)
            }
            mFlightSegmentAdapter.resetItems(mList as List<Any?>)
            bindingView.flightSegmentRecyclerView.layoutManager!!.scrollToPosition(scrollToPosition)
        }
    }

    companion object {
        const val ARA_ITEM_FLIGHTS_SEGMENT = "ARA_ITEM_FLIGHTS_SEGMENT"
        const val ARA_ITEM_FLIGHTS_SEGMENT_POSITION = "ARA_ITEM_FLIGHTS_SEGMENT_POSITION"
        const val ARA_ITEM_FLIGHTS_SEGMENT_DATA = "action_flightDetails_to_segment"
    }
}