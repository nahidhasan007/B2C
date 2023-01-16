package net.sharetrip.tour.history.detail

import android.graphics.Color
import androidx.fragment.app.viewModels
import androidx.navigation.fragment.findNavController
import net.sharetrip.shared.event.EventObserver
import net.sharetrip.shared.view.BaseFragment
import com.sharetrip.base.viewmodel.BaseViewModel
import net.sharetrip.tour.R
import net.sharetrip.tour.databinding.FragmentTourBookingHistoryDetailBinding
import net.sharetrip.tour.network.TourDataManager
import net.sharetrip.tour.utils.setTitleAndSubtitle
import net.sharetrip.tour.utils.setTripCoin

class TourBookingDetailFragment : BaseFragment<FragmentTourBookingHistoryDetailBinding>() {

    private val viewModel: TourBookingDetailViewModel by viewModels {
        val bookingCode =
            arguments?.getString(ARG_TOUR_BOOKING_CODE)
        TourBookingDetailVMF(
            bookingCode!!,
            TourBookingDetailRepo(TourDataManager.tourHistoryAPIService),
            TourDataManager.getSharedPref(requireContext())
        )
    }



    override fun layoutId(): Int = R.layout.fragment_tour_booking_history_detail

    override fun getViewModel(): BaseViewModel = viewModel

    override fun initOnCreateView() {
        setTitleAndSubtitle(getString(R.string.tour_history_details))
        setTripCoin()

        bindingView.lifecycleOwner = viewLifecycleOwner
        bindingView.viewModel = viewModel

        viewModel.historyItem.observe(viewLifecycleOwner){
            if (it.paymentStatus.equals("Cancelled") || it.paymentStatus.equals("UnPaid")){
                bindingView.textViewStatus.setTextColor(Color.RED)
            }
        }

        viewModel.navigateToDestinations.observe(viewLifecycleOwner, EventObserver {
            when (it.first) {
                TourBookingDetailViewModel.TourHistoryDetailNavDestinations.TO_PRICING -> {
                    findNavController().navigate(
                        R.id.action_tourBookingDetailFragment_to_tourPricingFragment,
                        it.second
                    )
                }
                TourBookingDetailViewModel.TourHistoryDetailNavDestinations.TO_INFO -> {
                    findNavController().navigate(
                        R.id.action_tourBookingDetailFragment_to_tourInfoFragment,
                        it.second
                    )
                }
                TourBookingDetailViewModel.TourHistoryDetailNavDestinations.TO_CONTACT -> {
                    findNavController().navigate(
                        R.id.action_tourBookingDetailFragment_to_tourContactInfoFragment,
                        it.second
                    )
                }
                TourBookingDetailViewModel.TourHistoryDetailNavDestinations.TO_CANCELLATION_POLICY -> {
                    findNavController().navigate(
                        R.id.action_tourBookingDetailFragment_to_tourCancellationPolicyFragment,
                        it.second
                    )
                }
            }
        })
    }

    companion object {
        const val ARG_TOUR_BOOKING_CODE = "TourBookingDetailScreen.TourItem"
    }
}
