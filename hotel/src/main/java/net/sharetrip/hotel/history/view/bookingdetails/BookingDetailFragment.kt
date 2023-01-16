package net.sharetrip.hotel.history.view.bookingdetails

import android.os.Bundle
import android.widget.Toast
import androidx.appcompat.app.AlertDialog
import androidx.core.content.ContextCompat
import androidx.lifecycle.ViewModelProvider
import androidx.lifecycle.lifecycleScope
import androidx.navigation.fragment.findNavController
import com.sharetrip.base.event.EventObserver
import net.sharetrip.shared.utils.ARG_PAYMENT_URL
import net.sharetrip.shared.utils.SERVICE_TYPE
import net.sharetrip.shared.utils.SERVICE_TYPE_HOTEL
import net.sharetrip.shared.utils.navigateSafe
import net.sharetrip.shared.view.BaseFragment
import com.sharetrip.base.viewmodel.BaseViewModel
import net.sharetrip.hotel.R
import net.sharetrip.hotel.booking.model.HotelBookingStatus
import net.sharetrip.hotel.booking.model.PaymentStatus
import net.sharetrip.hotel.databinding.FragmentBookingHistoryDetailBinding
import net.sharetrip.hotel.history.HotelHistoryActivity
import net.sharetrip.hotel.history.model.HotelHistoryItem
import net.sharetrip.hotel.history.view.historylist.HotelHistoryListFragment.Companion.ARG_HOTEL_HISTORY_ITEM
import net.sharetrip.hotel.network.DataManager

class BookingDetailFragment : BaseFragment<FragmentBookingHistoryDetailBinding>() {
    private val viewModel: BookingDetailViewModel by lazy {
        val hotelHistoryItem =
            arguments?.getParcelable<HotelHistoryItem>(ARG_HOTEL_HISTORY_ITEM) as HotelHistoryItem
        ViewModelProvider(
            this,
            HotelHistoryDetailsVMFactory(
                DataManager.getSharedPref(requireContext()),
                hotelHistoryItem,
                DataManager.hotelHistoryService
            )
        ).get(
            BookingDetailViewModel::class.java
        )
    }

    override fun layoutId(): Int = R.layout.fragment_booking_history_detail

    override fun getViewModel(): BaseViewModel = viewModel

    override fun initOnCreateView() {
        setTitle(getString(R.string.booking_detail))
        bindingView.viewModel = viewModel
        bindingView.lifecycleOwner = viewLifecycleOwner

        viewModel.showMessage.observe(viewLifecycleOwner, EventObserver {
            Toast.makeText(context, it, Toast.LENGTH_LONG).show()
        })

        viewModel.navigateUp.observe(viewLifecycleOwner) {
            findNavController().navigateUp()
        }

        viewModel.gotoPayment.observe(viewLifecycleOwner) { url ->
            url?.let {
                val bundle = Bundle()
                bundle.putString(ARG_PAYMENT_URL, url)
                bundle.putString(SERVICE_TYPE, SERVICE_TYPE_HOTEL)

                lifecycleScope.launchWhenResumed {
                    findNavController().navigateSafe(R.id.action_hotelHistoryDetailsFragment_to_payment_nav_graph, bundle)
                }
            }
        }

        viewModel.showCancelAlert.observe(viewLifecycleOwner) {
            val builder1 = context?.let { it1 -> AlertDialog.Builder(it1) }
            builder1?.setTitle(getString(R.string.cancel_booking))
            builder1?.setMessage(getString(R.string.confirm_cancel_booking))
            builder1?.setCancelable(true)

            builder1?.setPositiveButton(getString(R.string.yes)) { dialog, _ ->
                run {
                    dialog.cancel()
                    viewModel.onClickCancelBooking()
                }
            }

            builder1?.setNegativeButton(getString(R.string.no)) { dialog, _ -> dialog.cancel() }

            val alert11 = builder1?.create()
            alert11?.show()
        }

        viewModel.showStatusColor.observe(viewLifecycleOwner) {
            setStatusColor()
        }

        viewModel.navigateToPriceInfoFragment.observe(viewLifecycleOwner) {
            findNavController().navigateSafe(
                R.id.action_hotelHistoryDetailsFragment_to_priceInfoFragment,
                it
            )
        }
        viewModel.navigateToTravelerListFragment.observe(viewLifecycleOwner) {
            findNavController().navigateSafe(
                R.id.action_hotelHistoryDetailsFragment_to_travelerListFragment,
                it
            )
        }
        viewModel.navigateToHotelInfoFragment.observe(viewLifecycleOwner) {
            findNavController().navigateSafe(
                R.id.action_hotelHistoryDetailsFragment_to_historyHotelInfoFragment,
                it
            )
        }
    }

    private fun setStatusColor() {

        when (this.viewModel.hotelInfo.status) {
            HotelBookingStatus.CANCELLED.value, HotelBookingStatus.UNSUCCESS.value -> {
                bindingView.textViewStatusBooking.setTextColor(
                    ContextCompat.getColor(
                        requireContext(),
                        R.color.error_color
                    )
                )
            }
            HotelBookingStatus.COMPLETED.value -> {
                bindingView.textViewStatusBooking.setTextColor(
                    ContextCompat.getColor(
                        requireContext(),
                        R.color.mid_green
                    )
                )
            }
            HotelBookingStatus.WAITING.value -> {
                bindingView.textViewStatusBooking.setTextColor(
                    ContextCompat.getColor(
                        requireContext(),
                        R.color.orange_light
                    )
                )
            }
        }

        when (this.viewModel.hotelInfo.paymentStatus) {
            PaymentStatus.CANCELLED.value -> {
                bindingView.textViewPaymentStatus.setTextColor(
                    ContextCompat.getColor(
                        requireContext(),
                        R.color.error_color
                    )
                )
            }
            PaymentStatus.PAID.value -> {
                bindingView.textViewPaymentStatus.setTextColor(
                    ContextCompat.getColor(
                        requireContext(),
                        R.color.mid_green
                    )
                )
            }
            PaymentStatus.UNPAID.value -> {
                bindingView.textViewPaymentStatus.setTextColor(
                    ContextCompat.getColor(
                        requireContext(),
                        R.color.orange_light
                    )
                )
            }
        }
    }

    private fun setTitle(title: String) {
        (activity as HotelHistoryActivity).supportActionBar?.title = title
    }
}
