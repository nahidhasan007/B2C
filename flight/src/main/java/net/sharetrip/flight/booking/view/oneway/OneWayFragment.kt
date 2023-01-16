package net.sharetrip.flight.booking.view.oneway

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
import net.sharetrip.flight.booking.FlightBookingActivity.Companion.FLIGHT_PROMOTION_IMAGE
import net.sharetrip.flight.booking.model.ChildrenDOB
import net.sharetrip.flight.booking.model.FlightSearch
import net.sharetrip.flight.booking.model.TripType
import net.sharetrip.flight.booking.view.flightlist.FlightListFragment.Companion.ARG_FLIGHT_LIST_DATA
import net.sharetrip.flight.booking.view.multicity.MultiCityFragment.Companion.ARG_CLASS_NAME
import net.sharetrip.flight.databinding.FragmentOneWayBinding
import net.sharetrip.shared.model.CalenderData
import net.sharetrip.shared.utils.*
import net.sharetrip.shared.view.BaseFragment
import net.sharetrip.tracker.FlightTrackerActivity

class OneWayFragment : BaseFragment<FragmentOneWayBinding>() {
    private val viewModel by viewModels<OneWayViewModel>()
    var isOrigin = false
    var isDeepLinkHandled = false

    override fun layoutId(): Int = R.layout.fragment_one_way

    override fun getViewModel(): BaseViewModel = viewModel

    override fun initOnCreateView() {
        viewModel.promotionalImage = FLIGHT_PROMOTION_IMAGE
        bindingView.viewModel = viewModel

        bindingView.fromLayout.setOnClickListener {
            openSearch(true)
        }

        bindingView.toLayout.setOnClickListener {
            openSearch(false)
        }

        viewModel.moveToTravellers.observe(viewLifecycleOwner, EventObserver {
            findNavController().navigateSafe(
                    R.id.action_home_to_travellerNumber,
                    bundleOf(ARG_TRAVELLER to it)
            )
        })

        viewModel.onSearchFlightClicked.observe(viewLifecycleOwner, EventObserver {
            searchFlight()
        })

        getNavigationResultLiveData<Intent>(ARG_FLIGHT_SEARCH_AIRPORT)?.observe(viewLifecycleOwner) {
            if (it.getBooleanExtra(IS_ORIGIN_OR_DESTINATION, true))
                viewModel.handleFromAddress(it)
            else
                viewModel.handleToAddress(it)
        }

        getNavigationResultLiveData<Long>(ARG_SINGLE_DATE)?.observe(viewLifecycleOwner) {
            viewModel.handleDepartureDate(it)
        }

        getNavigationResultLiveData<Intent>(ARG_TRAVELLER)?.observe(viewLifecycleOwner) {
            it?.let {
                viewModel.handleTravellerData(it)
            }
        }

        bindingView.departureDateLayout.setOnClickListener {
            findNavController().navigateSafe(
                    R.id.action_home_to_single_date_calender,
                    bundleOf(
                            ARG_CALENDER_DATA to CalenderData(
                                    viewModel.startDateInMillisecond,
                                    mDateHintText = getString(R.string.departure_date),
                                    fromAirportCode = viewModel.fromAirPortCode.get()!!,
                                    toAirportCode = viewModel.toAirPortCode.get()!!
                            )
                    )
            )
        }

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
            if (uri.getQueryParameter("tripType").equals(TripType.ONE_WAY, true))
                handleDeepLink(uri)
    }

    private fun searchFlight() {
        if (NetworkUtil.hasNetwork(requireContext())) {
            findNavController().navigateSafe(
                    R.id.action_go_to_flight_list, bundleOf(
                    ARG_FLIGHT_LIST_DATA to viewModel.oneWayTripSearchModel)
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

            viewModel.oneWayTripSearchModel = searchModel
            viewModel.fromAirPortCode.set(searchModel.origin[0])
            viewModel.toAirPortCode.set(searchModel.destination[0])
            viewModel.departureDate.set(searchModel.depart[0].parseDateForDisplayFromApi())
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
        bundle.putString(ARG_CLASS_NAME, ARG_FLIGHT_SEARCH_AIRPORT)
        findNavController().navigateSafe(
                R.id.action_home_to_flight_search,
                bundleOf(ARG_FLIGHT_SEARCH_BUNDLE to bundle)
        )
    }

    companion object {
        const val ARG_FLIGHT_SEARCH_AIRPORT = "ARG_FLIGHT_SEARCH_AIRPORT"
        const val ARG_FLIGHT_SEARCH_BUNDLE = "ARG_FLIGHT_SEARCH_BUNDLE"
        const val IS_ORIGIN_OR_DESTINATION = "IS_ORIGIN_OR_DESTINATION"
    }
}
