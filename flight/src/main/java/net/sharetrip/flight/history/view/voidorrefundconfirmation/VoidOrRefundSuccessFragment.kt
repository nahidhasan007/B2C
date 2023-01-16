package net.sharetrip.flight.history.view.voidorrefundconfirmation

import androidx.navigation.fragment.findNavController
import net.sharetrip.shared.utils.capitalize
import net.sharetrip.shared.utils.setNavigationResult
import net.sharetrip.shared.view.BaseFragment
import com.sharetrip.base.viewmodel.BaseViewModel
import net.sharetrip.flight.R
import net.sharetrip.flight.databinding.FragmentRefundOrVoidSuccessBinding
import net.sharetrip.flight.history.view.refundselectpassenger.SelectPassengerFragment.Companion.ARG_REFUND_VOID_DATA
import net.sharetrip.flight.utils.setTitleSubTitle

class VoidOrRefundSuccessFragment : BaseFragment<FragmentRefundOrVoidSuccessBinding>() {

    override fun layoutId() = R.layout.fragment_refund_or_void_success

    override fun getViewModel(): BaseViewModel? = null

    override fun initOnCreateView() {
        val requestType =
            requireArguments().getBundle(ARG_REFUND_VOID_DATA)
                ?.getString(ARG_REFUND_VOID_DATA).toString()

        setTitleSubTitle(getString(R.string.booking) + " " + requestType.capitalize())

        bindingView.isVoid = requestType.lowercase() == "void"

        bindingView.goBackButton.setOnClickListener {
            if (requestType.lowercase() == "void") {
                setNavigationResult(requestType, WAS_REFUND_OR_VOID)
                findNavController().navigate(R.id.action_successFragment_to_flightBookingDetails_after_void)
            }
            else
                findNavController().navigate(R.id.action_successFragment_to_flightBookingDetails)
        }
    }

    companion object {
        const val WAS_REFUND_OR_VOID = "WAS_REFUND_OR_VOID"
    }
}
