package net.sharetrip.flight.history.view.voidorrefundconfirmation

import androidx.core.os.bundleOf
import androidx.navigation.fragment.findNavController
import com.sharetrip.base.data.PrefKey
import com.sharetrip.base.data.SharedPrefsHelper
import com.sharetrip.base.event.EventObserver
import net.sharetrip.shared.view.BaseFragment
import com.sharetrip.base.viewmodel.BaseViewModel
import net.sharetrip.flight.R
import net.sharetrip.flight.databinding.FragmentRefundOrVoidConfirmationBinding
import net.sharetrip.flight.history.model.RefundQuotationResponse
import net.sharetrip.flight.history.model.VoidQuotationResponse
import net.sharetrip.flight.history.view.list.FlightHistoryListFragment.Companion.ARG_FLIGHT_VOID_QUOTATION
import net.sharetrip.flight.history.view.refundselectpassenger.SelectPassengerFragment.Companion.ARG_REFUND_QUOTATION_RESPONSE
import net.sharetrip.flight.history.view.refundselectpassenger.SelectPassengerFragment.Companion.ARG_REFUND_VOID_DATA
import net.sharetrip.flight.network.DataManager
import net.sharetrip.flight.utils.setTitleSubTitle

class ConfirmationFragment : BaseFragment<FragmentRefundOrVoidConfirmationBinding>() {

    private val viewModel: ConfirmationViewModel by lazy {
        ConfirmationVIewModelFactory(
            DataManager.flightHistoryApiService,
            refundQuotationData?.refundSearchId,
            voidData?.voidSearchId,
            requestType,
            SharedPrefsHelper(requireContext())[PrefKey.ACCESS_TOKEN, ""]
        ).create(ConfirmationViewModel::class.java)
    }

    private var refundQuotationData: RefundQuotationResponse? = null
    private lateinit var requestType: String
    private var voidData: VoidQuotationResponse? = null

    override fun layoutId() = R.layout.fragment_refund_or_void_confirmation

    override fun getViewModel(): BaseViewModel = viewModel

    override fun initOnCreateView() {
        setTitleSubTitle(getString(R.string.confirmation))

        requestType =
            requireArguments().getBundle(ARG_REFUND_VOID_DATA)
                ?.getString(ARG_REFUND_VOID_DATA).toString()

        if (requestType.lowercase() == "refund")
            refundQuotationData = requireArguments().getBundle(ARG_REFUND_VOID_DATA)
                ?.getParcelable(ARG_REFUND_QUOTATION_RESPONSE)!!
        else {
            voidData = requireArguments().getBundle(ARG_REFUND_VOID_DATA)
                ?.getParcelable(ARG_FLIGHT_VOID_QUOTATION)
        }

        bindingView.isVoid = requestType.lowercase() == "void"
        bindingView.viewModel = viewModel

        bindingView.cancelButton.setOnClickListener {
            if (!viewModel.isLoading.get())
                findNavController().navigateUp()
        }

        viewModel.goSuccessScreen.observe(viewLifecycleOwner, EventObserver {
            findNavController().navigate(
                R.id.action_confirmationFragment_to_successFragment,
                bundleOf(ARG_REFUND_VOID_DATA to requireArguments().getBundle(ARG_REFUND_VOID_DATA))
            )
        })
    }
}
