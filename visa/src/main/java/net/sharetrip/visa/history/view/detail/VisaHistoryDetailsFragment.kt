package net.sharetrip.visa.history.view.detail

import android.widget.Toast
import androidx.appcompat.app.AlertDialog
import androidx.core.content.ContextCompat
import androidx.core.os.bundleOf
import androidx.fragment.app.viewModels
import androidx.lifecycle.lifecycleScope
import androidx.navigation.fragment.findNavController
import com.sharetrip.base.event.EventObserver
import net.sharetrip.shared.utils.VISA_BOOKING_STATUS
import net.sharetrip.shared.utils.VISA_PAYMENT_STATUS
import net.sharetrip.shared.utils.navigateSafe
import net.sharetrip.shared.view.BaseFragment
import com.sharetrip.base.viewmodel.BaseViewModel
import net.sharetrip.visa.R
import net.sharetrip.visa.databinding.FragmentVisaHistoryDetailsBinding
import net.sharetrip.visa.network.DataManager
import net.sharetrip.visa.utils.*

class VisaHistoryDetailsFragment : BaseFragment<FragmentVisaHistoryDetailsBinding>() {
    private val viewModel: VisaHistoryDetailsViewModel by viewModels() {
        val bookingCode = requireArguments().getString(ARG_VISA_BOOKING_CODE, "")
        VisaHistoryDetailViewModelFactory(
            bookingCode, DataManager.getSharedPref(requireContext()),
            VisaHistoryDetailRepository(DataManager.visaHistoryApiService)
        )
    }

    

    override fun layoutId() = R.layout.fragment_visa_history_details

    override fun getViewModel(): BaseViewModel = viewModel

    override fun initOnCreateView() {
        setTitleAndSubtitle(getString(R.string.booking_details))
        setToolbarTripCoin()

        bindingView.viewModel = viewModel
        bindingView.lifecycleOwner = viewLifecycleOwner

        viewModel.toastMessage.observe(viewLifecycleOwner) {
            Toast.makeText(context, it, Toast.LENGTH_LONG).show()
        }

        viewModel.showAlert.observe(viewLifecycleOwner) {
            val dialogBuilder = context?.let { it1 -> AlertDialog.Builder(it1) }
            dialogBuilder?.setTitle("Cancel Booking")
            dialogBuilder?.setMessage("Do you really want to cancel?")
            dialogBuilder?.setCancelable(true)

            dialogBuilder?.setPositiveButton("Yes") { dialog, _ ->
                run {
                    dialog.cancel()
                    viewModel.onClickCancelBooking()
                }
            }

            dialogBuilder?.setNegativeButton("No") { dialog, id -> dialog.cancel() }

            val alert11 = dialogBuilder?.create()
            alert11?.show()
        }

        viewModel.showStatusColor.observe(viewLifecycleOwner) {
            setStatusColor()
        }

        viewModel.navigateToCancellationPolicy.observe(viewLifecycleOwner, EventObserver {
            val bundle =
                bundleOf(VISA_HISTORY_CANCELLATION to viewModel.visaHistoryDetailsResponse.value?.visaProductSnapshot?.cancellationPolicy)
            findNavController().navigateSafe(
                R.id.action_visaHistoryDetailsFragment_to_visaHistoryCancellationFragment,
                bundle
            )
        })

        viewModel.navigateToPriceDetails.observe(viewLifecycleOwner, EventObserver {
            val bundle = bundleOf(
                VISA_PRODUCT_SNAPSHOT to viewModel.visaHistoryDetailsResponse.value?.visaProductSnapshot,
                VISA_TRAVELLER_COUNT to viewModel.visaHistoryDetailsResponse.value?.travellerCount!!,
                VISA_CONVENIENCE_FEE to viewModel.visaHistoryDetailsResponse.value?.convenienceFee,
                VISA_VAT_ON_CONVENIENCE_FEE to viewModel.visaHistoryDetailsResponse.value?.VATOnConvenienceFee
            )

            findNavController().navigateSafe(
                R.id.action_visaHistoryDetailsFragment_to_visaHistoryPriceDetailsFragment,
                bundle
            )
        })

        viewModel.navigateToContactInfo.observe(viewLifecycleOwner, EventObserver {
            val bundle =
                bundleOf(VISA_HISTORY_CONTACT to viewModel.visaHistoryDetailsResponse.value?.primaryContact!!)
            findNavController().navigateSafe(
                R.id.action_visaHistoryDetailsFragment_to_visaHistoryContactFragment,
                bundle
            )
        })

        viewModel.navigateToTravellerDetails.observe(viewLifecycleOwner, EventObserver{
            val bundle = bundleOf(VISA_HISTORY_TRAVELLERS to (viewModel.visaHistoryDetailsResponse.value?.travellers ?: listOf()))
            findNavController().navigateSafe(R.id.action_visaHistoryDetailsFragment_to_visaHistoryTravellerDetailsFragment, bundle)
        })

        viewModel.navigateToPayment.observe(viewLifecycleOwner, EventObserver { bundle ->
            lifecycleScope.launchWhenResumed {
                findNavController().navigateSafe(R.id.navigate_to_payment, bundle)
            }
        })
    }

    private fun setStatusColor() {

        when (viewModel.status.get()) {

            VISA_PAYMENT_STATUS.UNPAID.value -> {
                bindingView.textViewStatus.setTextColor(
                    ContextCompat.getColor(
                        requireContext(),
                        R.color.error_color
                    )
                )
            }

            VISA_BOOKING_STATUS.PENDING.value -> {
                bindingView.textViewStatus.setTextColor(
                    ContextCompat.getColor(
                        requireContext(),
                        R.color.example_7_yellow
                    )
                )
            }

            VISA_BOOKING_STATUS.CANCELLED.value -> {
                bindingView.textViewStatus.setTextColor(
                    ContextCompat.getColor(
                        requireContext(),
                        R.color.error_color
                    )
                )
            }

            VISA_BOOKING_STATUS.PROCESSING.value -> {
                bindingView.textViewStatus.setTextColor(
                    ContextCompat.getColor(
                        requireContext(),
                        R.color.clear_blue
                    )
                )
            }

            "Successful" -> {
                bindingView.textViewStatus.setTextColor(
                    ContextCompat.getColor(
                        requireContext(),
                        R.color.mid_green
                    )
                )
            }
        }
    }

    
}
