package net.sharetrip.flight.booking.view.roundtrip

import android.content.Intent
import android.net.Uri
import android.os.Bundle
import android.widget.Toast
import androidx.core.net.toUri
import androidx.core.os.bundleOf
import androidx.fragment.app.viewModels
import androidx.navigation.fragment.findNavController
import com.sharetrip.base.event.EventObserver
import com.sharetrip.base.network.NetworkUtil
import com.sharetrip.base.viewmodel.BaseViewModel
import net.sharetrip.flight.R
import net.sharetrip.flight.booking.model.ChildrenDOB
import net.sharetrip.flight.booking.model.FlightSearch
import net.sharetrip.flight.booking.model.TripType
import net.sharetrip.flight.booking.view.flightlist.FlightListFragment
import net.sharetrip.flight.booking.view.multicity.MultiCityFragment.Companion.ARG_CLASS_NAME
import net.sharetrip.flight.booking.view.oneway.OneWayFragment.Companion.ARG_FLIGHT_SEARCH_BUNDLE
import net.sharetrip.flight.booking.view.oneway.OneWayFragment.Companion.IS_ORIGIN_OR_DESTINATION
import net.sharetrip.flight.databinding.FragmentRoundTripBinding
import net.sharetrip.shared.model.CalenderData
import net.sharetrip.shared.utils.*
import net.sharetrip.shared.view.BaseFragment
import net.sharetrip.tracker.FlightTrackerActivity

class RoundTripFragment : BaseFragment<FragmentRoundTripBinding>() {

    private val viewModel by viewModels<RoundTripViewModel>()
    private var isOrigin = true
    private var isDeepLinkHandled = false

    override fun layoutId(): Int = R.layout.fragment_round_trip

    override fun getViewModel(): BaseViewModel = viewModel

    override fun initOnCreateView() {
        bindingView.viewModel = viewModel

        bindingView.fromLayout.setOnClickListener {
            openSearch(true)
        }

        bindingView.toLayout.setOnClickListener {
            openSearch(false)
        }

        getNavigationResultLiveData<Intent>(ARG_RANGE_DATE)?.observe(viewLifecycleOwner) {
            viewModel.handleDepartureAndReturnDate(it)
        }

        getNavigationResultLiveData<Intent>(ARG_FLIGHT_SEARCH_AIRPORT)?.observe(viewLifecycleOwner) {
            if (it.getBooleanExtra(IS_ORIGIN_OR_DESTINATION, true))
                viewModel.handleFromAddress(it)
            else
                viewModel.handleToAddress(it)
        }

        getNavigationResultLiveData<Intent>(ARG_TRAVELLER)?.observe(viewLifecycleOwner) {
            it?.let {
                viewModel.handleTravellerData(it)
            }
        }

        bindingView.departureDateLayout.setOnClickListener {
            findNavController().navigateSafe(
                R.id.action_home_to_range_date_calender,
                bundleOf(
                    ARG_CALENDER_DATA to CalenderData(
                        viewModel.startDateInMillisecond,
                        viewModel.endDateInMillisecond,
                        getString(R.string.departure_date),
                        getString(R.string.return_date),
                        fromAirportCode = viewModel.fromAirPortCode.get()!!,
                        toAirportCode = viewModel.toAirPortCode.get()!!,
                        serviceType = "flight"
                    )
                )
            )
        }

        bindingView.travelersAndClassLayout.setOnClickListener {
            findNavController().navigateSafe(
                R.id.action_home_to_travellerNumber,
                bundleOf(ARG_TRAVELLER to viewModel.travellers)
            )
        }

        viewModel.onSearchClicked.observe(viewLifecycleOwner, EventObserver {
            searchFlight()
        })

        viewModel.navigateToTravelAdvice.observe(viewLifecycleOwner, EventObserver {
            val intent = Intent(context, FlightTrackerActivity::class.java)
            intent.putExtra(
                FlightTrackerActivity.FLIGHT_TRACKER_ACTION,
                FlightTrackerActivity.Companion.FlightTrackerAction.TRIP_ADVISOR
            )
            intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP)
            startActivity(intent)
        })

        val uri = requireActivity().intent.data
        if (uri != null && !isDeepLinkHandled)
            if (uri.getQueryParameter("tripType").equals(TripType.ROUND_TRIP, true))
                handleDeepLink(uri)
    }

    private fun searchFlight() {
        if (NetworkUtil.hasNetwork(requireContext())) {
            findNavController().navigateSafe(
                    R.id.action_go_to_flight_list, bundleOf(
                    FlightListFragment.ARG_FLIGHT_LIST_DATA to viewModel.roundTripSearchModel
            )
            )
        } else {
            Toast.makeText(context, "No Internet", Toast.LENGTH_LONG).show()
        }
    }

    private fun handleDeepLink(uri: Uri) {
        try {
            val searchModel = FlightSearch(origin = uri.getQueryParameters("origin").toMutableList(),
                    originCity = uri.getQueryParameters("originCity").toMutableList(),
                    originAddress = uri.getQueryParameters("originAirport").toMutableList(),
                    destination = uri.getQueryParameters("destination").toMutableList(),
                    destinationCity = uri.getQueryParameters("destinationCity").toMutableList(),
                    destinationAddress = uri.getQueryParameters("destinationAirport").toMutableList(),
                    depart = uri.getQueryParameters("depart").toMutableList(),
                    tripType = uri.getQueryParameter("tripType")!!,
                    classType = uri.getQueryParameter("class")!!,
                    adult = uri.getQueryParameter("adult")!!.toInt(),
                    child = uri.getQueryParameter("child")!!.toInt(),
                    childDateOfBirthList = uri.toString().replace("[]", "").toUri().getQueryParameters("childAge")
                            .mapIndexed { index, s -> ChildrenDOB(title = "Child ${index + 1} Date of birth", date = s) } as ArrayList<ChildrenDOB>,
                    infant = uri.getQueryParameter("infant")!!.toInt())

            viewModel.roundTripSearchModel = searchModel
            viewModel.fromAirPortCode.set(searchModel.origin[0])
            viewModel.toAirPortCode.set(searchModel.destination[0])
            viewModel.roundDepartureDate.set("${searchModel.depart[0].parseDateForDisplayFromApi()} - ${searchModel.depart[1].parseDateForDisplayFromApi()}")
            viewModel.travelersAndClassCount.set("${searchModel.adult + searchModel.child + searchModel.infant} Traveller(s) - ${searchModel.classType}")

            searchFlight()
        } catch (e: Exception) {
            e.printStackTrace()
            Toast.makeText(requireContext(), "Invalid Search Input! Try filling all fields", Toast.LENGTH_SHORT).show()
        }
        isDeepLinkHandled = true
    }


    private fun openSearch(isOrigin: Boolean) {
        val bundle = Bundle()
        bundle.putBoolean(IS_ORIGIN_OR_DESTINATION, isOrigin)
        bundle.putString(ARG_FLIGHT_SEARCH_TITLE_TEXT, getString(R.string.origin_city_or_Airport))
        bundle.putString(ARG_FLIGHT_SEARCH_AIRPORT, getString(R.string.origin_city_or_Airport))
        bundle.putString(ARG_CLASS_NAME, ARG_FLIGHT_SEARCH_AIRPORT)
        findNavController().navigateSafe(
            R.id.action_home_to_flight_search,
            bundleOf(ARG_FLIGHT_SEARCH_BUNDLE to bundle)
        )
    }

    companion object {
        const val ARG_FLIGHT_SEARCH_AIRPORT = "ARG_FLIGHT_SEARCH_AIRPORT"
    }
}
