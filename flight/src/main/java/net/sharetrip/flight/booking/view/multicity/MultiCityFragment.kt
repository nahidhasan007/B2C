package net.sharetrip.flight.booking.view.multicity

import android.content.Intent
import android.os.Bundle
import android.widget.Toast
import androidx.core.content.ContextCompat
import androidx.core.os.bundleOf
import androidx.navigation.fragment.findNavController
import androidx.recyclerview.widget.DividerItemDecoration
import com.sharetrip.base.event.EventObserver
import net.sharetrip.shared.model.CalenderData
import com.sharetrip.base.network.NetworkUtil
import net.sharetrip.shared.view.BaseFragment
import com.sharetrip.base.viewmodel.BaseViewModel
import net.sharetrip.flight.R
import net.sharetrip.flight.booking.model.FlightSearch
import net.sharetrip.flight.booking.model.MultiCityModel
import net.sharetrip.flight.booking.model.TravellersInfo
import net.sharetrip.flight.booking.view.multicity.MultiCityViewModel.Companion.ARG_FLIGHT_LIST_DATA
import net.sharetrip.flight.booking.view.multicity.MultiCityViewModel.Companion.GOTO_FLIGHT_LIST
import net.sharetrip.flight.booking.view.multicity.MultiCityViewModel.Companion.GOTO_TRAVELLER
import net.sharetrip.flight.booking.view.multicity.MultiCityViewModel.Companion.PICK_DEPARTURE_DATE_REQUEST
import net.sharetrip.flight.booking.view.oneway.OneWayFragment
import net.sharetrip.flight.databinding.FragmentMultiCityBinding
import net.sharetrip.shared.utils.*
import net.sharetrip.tracker.FlightTrackerActivity

class MultiCityFragment : BaseFragment<FragmentMultiCityBinding>() {
    private val viewModel: MultiCityViewModel by lazy {
        MultiCityViewModelFactory(
            getString(R.string.origin_city_or_Airport),
            getString(R.string.destination_city_or_airport),
            getString(R.string.origin_city_or_Airport),
            getString(R.string.departure_date)
        ).create(MultiCityViewModel::class.java)
    }

    private val mMultiCityAdapter: MultiCityAdapter by lazy {
        MultiCityAdapter(
            mutableListOf(MultiCityModel(), MultiCityModel()),
            viewModel::onFromItemClick,
            viewModel::onToItemClick,
            viewModel::onDateItemClick
        )
    }

    private var isOrigin = true

    override fun layoutId(): Int = R.layout.fragment_multi_city

    override fun getViewModel(): BaseViewModel = viewModel

    override fun initOnCreateView() {
        viewModel.promotionalImage = ""
        bindingView.viewModel = viewModel

        bindingView.multiCityRecyclerView.adapter = mMultiCityAdapter
        val mDecoration = DividerItemDecoration(activity, DividerItemDecoration.VERTICAL)
        val mItemDrawable =
            ContextCompat.getDrawable(requireActivity(), R.drawable.space_item_decorator_vertical)
        mDecoration.setDrawable(mItemDrawable!!)
        bindingView.multiCityRecyclerView.addItemDecoration(mDecoration)

        viewModel.isAirportLayoutClicked.observe(viewLifecycleOwner, EventObserver {
            isOrigin = it
            openSearch()
        })

        viewModel.navigateToDestination.observe(viewLifecycleOwner, EventObserver {
            when (it.first) {
                PICK_DEPARTURE_DATE_REQUEST ->
                    findNavController().navigateSafe(
                        R.id.action_home_to_single_date_calender,
                        bundleOf(ARG_CALENDER_DATA to it.second as CalenderData)
                    )

                GOTO_FLIGHT_LIST ->
                    if (NetworkUtil.hasNetwork(requireContext())) {
                        findNavController().navigateSafe(
                            R.id.action_go_to_flight_list, bundleOf(
                                ARG_FLIGHT_LIST_DATA to (it.second as FlightSearch)
                            )
                        )
                    } else {
                        Toast.makeText(context, "No Internet", Toast.LENGTH_LONG).show()
                    }

                GOTO_TRAVELLER ->
                    findNavController().navigateSafe(
                        R.id.action_home_to_travellerNumber,
                        bundleOf(ARG_TRAVELLER to it.second as TravellersInfo)
                    )
            }
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

        viewModel.isRemoveItem.observe(viewLifecycleOwner, EventObserver {
            if (it) {
                mMultiCityAdapter.removeItem()
            } else {
                mMultiCityAdapter.addItem()
            }
        })

        viewModel.changeItemAtPos.observe(viewLifecycleOwner) {
            mMultiCityAdapter.changeItemAtPos(it.first, it.second)
        }

        getNavigationResultLiveData<Intent>(ARG_FLIGHT_SEARCH_MULTI_CITY)?.observe(
            viewLifecycleOwner
        ) {
            if (isOrigin) {
                viewModel.handleFromAddress(it)
            } else {
                viewModel.handleToAddress(it)
            }
        }

        getNavigationResultLiveData<Long>(ARG_SINGLE_DATE)?.observe(viewLifecycleOwner) {
            viewModel.handleDepartureDate(it)
        }

        getNavigationResultLiveData<Intent>(ARG_TRAVELLER)?.observe(viewLifecycleOwner) {
            it?.let {
                viewModel.handleTravellerData(it)
            }
        }

    }

    private fun openSearch() {
        val bundle = Bundle()
        bundle.putString(ARG_FLIGHT_SEARCH_TITLE_TEXT, getString(R.string.origin_city_or_Airport))
        bundle.putString(ARG_CLASS_NAME, ARG_FLIGHT_SEARCH_MULTI_CITY)
        findNavController().navigateSafe(
            R.id.action_home_to_flight_search,
            bundleOf(OneWayFragment.ARG_FLIGHT_SEARCH_BUNDLE to bundle)
        )
    }

    companion object {
        const val ARG_FLIGHT_SEARCH_MULTI_CITY = "ARG_FLIGHT_SEARCH_MULTI_CITY"
        const val ARG_CLASS_NAME = "ARG_CLASS_NAME"
    }
}
