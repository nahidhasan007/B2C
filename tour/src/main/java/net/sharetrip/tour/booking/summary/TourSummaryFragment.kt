package net.sharetrip.tour.booking.summary

import android.os.Bundle
import android.view.Menu
import android.view.MenuInflater
import android.widget.Toast
import androidx.fragment.app.viewModels
import net.sharetrip.shared.view.BaseFragment
import com.sharetrip.base.viewmodel.BaseViewModel
import net.sharetrip.tour.R
import net.sharetrip.tour.databinding.FragmentTourSummaryBinding
import net.sharetrip.tour.model.TourBookingParam
import net.sharetrip.tour.model.TourSummary
import net.sharetrip.tour.network.TourDataManager
import net.sharetrip.tour.utils.setTitleAndSubtitle
import net.sharetrip.tour.utils.setTripCoin

class TourSummaryFragment : BaseFragment<FragmentTourSummaryBinding>() {

    private val viewModel: TourSummaryViewModel by viewModels {
        val bookingParam = arguments?.getParcelable<TourBookingParam>(ARG_BOOKING_PARAM_MODEL)
        val tourSummary = arguments?.getParcelable<TourSummary>(ARG_TOUR_SUMMARY_MODEL)
        TourSummaryVMF(
            bookingParam!!,
            tourSummary!!,
            TourDataManager.getSharedPref(requireContext()),
            TourSummaryRepo(TourDataManager.tourBookingAPIService)
        )
    }

    private val adapter = PaymentAdapter()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setHasOptionsMenu(true)
    }

    override fun layoutId(): Int = R.layout.fragment_tour_summary

    override fun getViewModel(): BaseViewModel = viewModel

    override fun initOnCreateView() {
        setTitleAndSubtitle(getString(R.string.tour_summary))
        setTripCoin()

        bindingView.lifecycleOwner = viewLifecycleOwner
        bindingView.viewModel = viewModel
        bindingView.bottomSheet.viewModel = viewModel
        bindingView.seekBar.isEnabled = false
        bindingView.listPaymentType.adapter = adapter
        bindingView.seekBar.setOnSeekBarChangeListener(viewModel.seekBarListener)

        viewModel.paymentList.observe(viewLifecycleOwner) {
            adapter.update(it as ArrayList<net.sharetrip.payment.model.PaymentMethod>)
            if (it.isNotEmpty()) {
                adapter.selectedItem(it[0])
            }
        }

        adapter.selectedPaymentId.observe(viewLifecycleOwner) {
            viewModel.setPaymentGateWayId(it)
        }

        viewModel.showMsg.observe(viewLifecycleOwner) {
            Toast.makeText(context, it, Toast.LENGTH_LONG).show()
        }

        viewModel.redeemChecked.observe(viewLifecycleOwner) {
            if (it) {
                bindingView.seekBar.progress = 0
                bindingView.seekBar.isEnabled = true
            } else {
                bindingView.seekBar.progress = 0
                bindingView.seekBar.isEnabled = false
            }
        }
    }

    override fun onCreateOptionsMenu(menu: Menu, inflater: MenuInflater) {
        menu.clear()
        super.onCreateOptionsMenu(menu, inflater)
    }

    companion object {
        const val ARG_BOOKING_PARAM_MODEL = "TourSummaryScreen.BookingParam.Model"
        const val ARG_TOUR_SUMMARY_MODEL = "TourSummaryScreen.TourSummary.Model"
    }
}
