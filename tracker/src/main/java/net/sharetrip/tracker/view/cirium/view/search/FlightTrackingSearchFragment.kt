package net.sharetrip.tracker.view.cirium.view.search

import android.widget.Toast
import androidx.core.os.bundleOf
import androidx.navigation.fragment.findNavController
import com.sharetrip.base.event.EventObserver
import net.sharetrip.shared.view.BaseFragment
import com.sharetrip.base.viewmodel.BaseViewModel
import net.sharetrip.shared.utils.*
import net.sharetrip.tracker.R
import net.sharetrip.tracker.databinding.FragmentFlightTrackingSearchBinding
import net.sharetrip.tracker.network.DataManager

class FlightTrackingSearchFragment : BaseFragment<FragmentFlightTrackingSearchBinding>() {

    private val viewModel by lazy {
        FlightTrackingSearchVMFactory(FlightTrackingSearchRepo(DataManager.flightTrackerApiService)).create(
            FlightTrackingSearchViewModel::class.java
        )
    }



    override fun layoutId() = R.layout.fragment_flight_tracking_search

    override fun getViewModel(): BaseViewModel = viewModel

    override fun initOnCreateView() {
        bindingView.lifecycleOwner = viewLifecycleOwner
        bindingView.viewModel = viewModel

        viewModel.message.observe(viewLifecycleOwner) {
            Toast.makeText(requireContext(), it, Toast.LENGTH_SHORT).show()
        }

        viewModel.navigateToFlightList.observe(viewLifecycleOwner, EventObserver {
            findNavController().navigateSafe(R.id.action_flightTrackingSearchFragment_to_flightStatusListFragment)
        })

        viewModel.navigateToCalender.observe(viewLifecycleOwner, EventObserver {
            val bundle = bundleOf(ARG_CALENDER_DATA to it)
            findNavController().navigateSafe(
                R.id.action_flightTrackingSearchFragment_to_trackerCalenderFragment,
                bundle
            )
        })

        viewModel.navigateToFlightDetails.observe(viewLifecycleOwner, EventObserver {
            val bundle = bundleOf(ARG_FLIGHT_TRACKING_DATA to viewModel.trackingDataHolder)
            findNavController().navigateSafe(
                R.id.action_flightTrackingSearchFragment_to_flightStatusDetailsFragment,
                bundle
            )
        })

        viewModel.hideKeyBoard.observe(viewLifecycleOwner) {
            if (it) {
                hideKeyboard()
            }
        }

        getNavigationResultLiveData<Long>(ARG_SINGLE_DATE)?.observe(viewLifecycleOwner) {
            viewModel.setDepartureDate(it)
        }
    }

    

    override fun onStart() {
        super.onStart()
        hideTripCoin()
    }

    override fun onStop() {
        super.onStop()
        showTripCoin()
    }

    companion object {
        const val ARG_FLIGHT_TRACKING_DATA = "ARG_FLIGHT_TRACKING_DATA"
    }
}
