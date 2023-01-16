package net.sharetrip.hotel.booking.view.main

import android.content.Intent
import android.os.Bundle
import android.widget.Toast
import androidx.core.os.bundleOf
import androidx.navigation.fragment.findNavController
import com.sharetrip.base.data.PrefKey
import com.sharetrip.base.event.EventObserver
import com.sharetrip.base.network.NetworkUtil
import com.sharetrip.base.viewmodel.BaseViewModel
import net.sharetrip.hotel.R
import net.sharetrip.hotel.booking.model.NavigationKey
import net.sharetrip.hotel.booking.model.TravellerRoomInfo
import net.sharetrip.hotel.booking.view.hotellist.HotelListFragment.Companion.ARG_HOTEL_QUERY_MODEL
import net.sharetrip.hotel.booking.view.main.HotelBookingMainViewModel.Companion.ARG_CALENDER_DATA
import net.sharetrip.hotel.booking.view.search.HotelSearchFragment.Companion.ARG_HOTEL_PROPERTY
import net.sharetrip.hotel.booking.view.travelleroom.TravellerRoomFragment.Companion.ARG_TRAVELER_DATA
import net.sharetrip.hotel.booking.view.travelleroom.TravellerRoomFragment.Companion.ARG_TRAVELER_ROOM_LIST
import net.sharetrip.hotel.databinding.FragmentHotelBookingMainBinding
import net.sharetrip.hotel.network.DataManager
import net.sharetrip.hotel.utils.HotelSearchQuery
import net.sharetrip.shared.model.CalenderData
import net.sharetrip.shared.utils.*
import net.sharetrip.shared.view.BaseFragment
import java.text.NumberFormat
import java.util.*

class HotelBookingMainFragment : BaseFragment<FragmentHotelBookingMainBinding>() {
    private val viewModel by lazy {
        HotelBookingMainViewModelFactory(DataManager.getSharedPref(requireContext())).create(
            HotelBookingMainViewModel::class.java
        )
    }
    private lateinit var adapter: WishListTripAdapter

    override fun layoutId() = R.layout.fragment_hotel_booking_main

    override fun getViewModel(): BaseViewModel = viewModel

    override fun initOnCreateView() {
        clearQuery()
        var points = DataManager.getSharedPref(requireContext())[PrefKey.USER_TRIP_COIN, ""]
        points = points.filter { it in '0'..'9' }
        setTripCoin(
            if (points.isEmpty())
                "0"
            else
                NumberFormat.getNumberInstance(Locale.US).format(points.toInt())
        )
        setTitleAndSubtitle(getString(R.string.title_hotels), "")
        bindingView.viewModel = viewModel
        bindingView.lifecycleOwner = viewLifecycleOwner

        viewModel.propertySelection.observe(viewLifecycleOwner, EventObserver {
            if (!it)
                Toast.makeText(context, getString(R.string.select_destination), Toast.LENGTH_SHORT)
                    .show()
        })

        viewModel.noInternet.observe(viewLifecycleOwner) {
            if (it)
                Toast.makeText(context, getString(R.string.no_internet), Toast.LENGTH_LONG).show()
        }

        adapter = WishListTripAdapter(viewModel)
        //bindingView.listTourCity.adapter = adapter

        viewModel.liveCities.observe(viewLifecycleOwner) {
            adapter.update(it)
        }

        viewModel.scrollingPosition.observe(viewLifecycleOwner) {
            // bindingView.listTourCity.scrollToPosition(it)
        }

        viewModel.navigateToDestination.observe(viewLifecycleOwner, EventObserver {
            when (it.first) {
                NavigationKey.Search.name -> {
                    findNavController().navigateSafe(R.id.action_hotel_main_to_hotel_search)
                }

                NavigationKey.Calender.name -> {
                    setTitleAndSubtitle(getString(R.string.select_date), "")
                    findNavController().navigateSafe(
                        R.id.action_hotel_main_to_date_range,
                        bundleOf(ARG_CALENDER_DATA to it.second as CalenderData)
                    )
                }

                NavigationKey.List.name -> {
                    if (NetworkUtil.hasNetwork(requireContext()))
                        findNavController().navigateSafe(
                            R.id.action_hotel_main_to_hotel_list,
                            bundleOf(ARG_HOTEL_QUERY_MODEL to it.second as HotelSearchQuery)
                        )
                    else
                        viewModel.noInternet.value = true
                }

                NavigationKey.Travellers.name -> {
                    val bundle = Bundle()
                    bundle.putParcelableArrayList(
                        ARG_TRAVELER_ROOM_LIST,
                        it.second as ArrayList<TravellerRoomInfo>
                    )
                    findNavController().navigateSafe(
                        R.id.action_hotel_main_to_traveller_room,
                        bundleOf(ARG_TRAVELER_DATA to bundle)
                    )
                }
            }
        })

        getNavigationResultLiveData<Intent>(ARG_HOTEL_PROPERTY)?.observe(viewLifecycleOwner) {
            viewModel.handleProperty(it)
            findNavController().currentBackStackEntry?.savedStateHandle?.remove<Intent>(
                ARG_HOTEL_PROPERTY
            )
        }

        getNavigationResultLiveData<Intent>(ARG_RANGE_DATE)?.observe(viewLifecycleOwner) {
            viewModel.handleCheckInCheckOutDate(it)
            findNavController().currentBackStackEntry?.savedStateHandle?.remove<Intent>(
                ARG_RANGE_DATE
            )
        }

        getNavigationResultLiveData<Intent>(ARG_TRAVELER_DATA)?.observe(viewLifecycleOwner) {
            viewModel.handleRoomAndTravelers(it)
            findNavController().currentBackStackEntry?.savedStateHandle?.remove<Intent>(
                ARG_TRAVELER_DATA
            )
        }
    }

    private fun clearQuery() {
        viewModel.queryModel.meals = ""
        viewModel.queryModel.amenities = ""
        viewModel.queryModel.propertyType = ""
        viewModel.queryModel.sort = ""
        viewModel.queryModel.neighborhoods = ""
        viewModel.queryModel.pointofinterest = ""
    }
}
