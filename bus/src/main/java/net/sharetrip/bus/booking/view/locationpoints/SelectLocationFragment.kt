package net.sharetrip.bus.booking.view.locationpoints

import android.widget.Toast
import androidx.core.os.bundleOf
import androidx.lifecycle.ViewModelProvider
import androidx.navigation.fragment.findNavController
import androidx.viewpager.widget.ViewPager
import net.sharetrip.shared.view.BaseFragment
import com.sharetrip.base.viewmodel.BaseViewModel
import net.sharetrip.bus.R
import net.sharetrip.bus.booking.model.Departure
import net.sharetrip.bus.booking.model.GuestPopUpData
import net.sharetrip.bus.booking.model.SearchIdAndTripCoin
import net.sharetrip.bus.booking.view.passengerInfo.BusPassengerInfoFragment.Companion.ARG_BUS_PASSENGER_INFO_BUNDLE
import net.sharetrip.bus.booking.view.seatselection.ItemBusHistoryPricingSeatView
import net.sharetrip.bus.databinding.FragmentSelectBusPointsBinding
import net.sharetrip.bus.history.model.SeatClassPrice
import net.sharetrip.bus.network.DataManager
import net.sharetrip.bus.utils.getAlertDialog

class SelectLocationFragment : BaseFragment<FragmentSelectBusPointsBinding>() {
    private val viewModel by lazy {
        val searchIdAndTripCoin =
            bundle.getParcelable<SearchIdAndTripCoin>(ARG_BUS_SELECTION_SEARCH_ID_TRIP_COIN)!!
        val departureInfo =
            bundle.getParcelable<Departure>(ARG_DEPARTURE_MODEL)!!

        ViewModelProvider(
            this,
            SelectionLocationVMFactory(
                departureInfo,
                searchIdAndTripCoin,
                DataManager.busBookingApiService, DataManager.getSharedPref(requireContext())
            )
        ).get(
            SelectLocationPointsViewModel::class.java
        )
    }

    val bundle by lazy { arguments?.getBundle(ARG_BUS_LOCATION_BUNDLE)!! }

    private val blurData: GuestPopUpData by lazy {
        GuestPopUpData(
            R.string.common_title,
            R.string.to_continue,
            R.drawable.ic_bus_44x44, viewModel
        )
    }

    override fun layoutId() = R.layout.fragment_select_bus_points

    override fun getViewModel(): BaseViewModel = viewModel

    override fun initOnCreateView() {
        bindingView.model = viewModel
        bindingView.data = blurData
        bindingView.lifecycleOwner = viewLifecycleOwner

        updatePricingDetail()
        val profDialog = getAlertDialog(layoutInflater = layoutInflater, requireContext())

        bindingView.viewPager.addOnPageChangeListener(object :
            ViewPager.SimpleOnPageChangeListener() {
            override fun onPageScrolled(
                position: Int, positionOffset: Float, positionOffsetPixels: Int
            ) {
                viewModel.selected = position
            }
        })

        viewModel.isNextClicked.observe(viewLifecycleOwner) {
            if (it) {
                bindingView.tabLayout.setScrollPosition(viewModel.selected, 0f, true)
                bindingView.viewPager.currentItem = viewModel.selected
            }
        }

        viewModel.errorMessage.observe(viewLifecycleOwner) {
            Toast.makeText(requireContext(), it, Toast.LENGTH_SHORT).show()
        }

        viewModel.dialogLoading.observe(viewLifecycleOwner) {
            if (it)
                profDialog.show()
            else
                profDialog.dismiss()
        }

        viewModel.isLoginObserver.observe(viewLifecycleOwner) {
            if (it) {
                setAdapters()
                viewModel.isLoggedIn.set(true)
            }
        }

        viewModel.navigateToPassengerInfo.observe(viewLifecycleOwner) {
            findNavController().navigate(
                R.id.action_boardingPointsFragment_to_busPassengerInfoFragment,
                bundleOf(ARG_BUS_PASSENGER_INFO_BUNDLE to it)
            )
        }
    }

    /*        override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        super.onActivityResult(requestCode, resultCode, data)
        if (requestCode == 111) {
            viewModel.fetchUserProfile()
        }
        //TODO login
    }*/

    private fun setAdapters() {
        val pointsPagerAdapter = activity?.supportFragmentManager?.let { fragmentManager ->
            PointsPagerAdapter(fragmentManager, viewModel)
        }
        pointsPagerAdapter?.let {
            bindingView.viewPager.adapter = it
            bindingView.tabLayout.setupWithViewPager(bindingView.viewPager)
        }
    }

    fun updatePricingDetail() {
        if (viewModel.departureInfo.selectedSeatsInfo?.size!! > 0) {
            viewModel.departureInfo.selectedSeatsInfo!!.forEach {
                val child = ItemBusHistoryPricingSeatView(requireContext())
                child.binding.allData =
                    SeatClassPrice(it.seatNo, it.seatTypeId, it.fare.originalFare)
                bindingView.layoutBookingPlan.addView(child)
            }
        }
    }

    companion object {
        const val ARG_BUS_LOCATION_BUNDLE = "ARG_BUS_LOCATION_BUNDLE"
        const val ARG_BUS_SELECTION_SEARCH_ID_TRIP_COIN = "ARG_BUS_SELECTION_SEARCH_ID_TRIP_COIN"
        const val ARG_DEPARTURE_MODEL = "ARG_DEPARTURE_MODEL"
    }
}
