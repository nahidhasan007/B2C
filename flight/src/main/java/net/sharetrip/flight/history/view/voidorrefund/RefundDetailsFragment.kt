package net.sharetrip.flight.history.view.voidorrefund

import android.os.Bundle
import androidx.core.os.bundleOf
import androidx.navigation.fragment.findNavController
import net.sharetrip.shared.view.BaseFragment
import com.sharetrip.base.viewmodel.BaseViewModel
import net.sharetrip.flight.R
import net.sharetrip.flight.databinding.FragmentRefundPricingDetailsBinding
import net.sharetrip.flight.history.model.RefundQuotationResponse
import net.sharetrip.flight.history.model.Traveller
import net.sharetrip.flight.history.view.refundselectpassenger.SelectPassengerFragment.Companion.ARG_REFUND_QUOTATION_RESPONSE
import net.sharetrip.flight.history.view.refundselectpassenger.SelectPassengerFragment.Companion.ARG_REFUND_VOID_DATA
import net.sharetrip.flight.history.view.refundselectpassenger.SelectPassengerFragment.Companion.ARG_SELECTED_PASSENGERS
import net.sharetrip.flight.utils.setTitleSubTitle

class RefundDetailsFragment : BaseFragment<FragmentRefundPricingDetailsBinding>() {

    override fun layoutId(): Int = R.layout.fragment_refund_pricing_details

    override fun getViewModel(): BaseViewModel? = null

    override fun initOnCreateView() {
        setTitleSubTitle(getString(R.string.refund_details))
        val data = requireArguments().getBundle(ARG_REFUND_VOID_DATA)
            ?.getParcelable<RefundQuotationResponse>(ARG_REFUND_QUOTATION_RESPONSE)
        val travellers = requireArguments().getBundle(ARG_REFUND_VOID_DATA)
            ?.getParcelableArrayList<Traveller>(ARG_SELECTED_PASSENGERS)

        data?.calculatePricing()
        bindingView.pricingDetails = data

        bindingView.travellersInformationButton.setOnClickListener {
            val bundle = Bundle()
            bundle.putParcelableArrayList(ARG_SELECTED_PASSENGERS, travellers)

            findNavController().navigate(
                R.id.action_pricingDetailsFragment_to_refundTravellerDetailsFragment,
                bundleOf(ARG_SELECTED_PASSENGERS to bundle)
            )
        }

        bindingView.nextButton.setOnClickListener {
            val bundle = Bundle()
            bundle.putParcelable(ARG_REFUND_QUOTATION_RESPONSE, data)
            bundle.putString(ARG_REFUND_VOID_DATA, "refund")

            findNavController().navigate(
                R.id.action_pricingDetailsFragment_to_confirmationFragment,
                bundleOf(ARG_REFUND_VOID_DATA to bundle)
            )
        }
    }
}
