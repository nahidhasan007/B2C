package net.sharetrip.flight.history.view.priceinfo

import net.sharetrip.shared.view.BaseFragment
import com.sharetrip.base.viewmodel.BaseViewModel
import net.sharetrip.flight.R
import net.sharetrip.flight.booking.model.flightresponse.flights.price.PriceBreakdown
import net.sharetrip.flight.databinding.FragmentPriceInformationBinding
import net.sharetrip.flight.utils.setTitleSubTitle

class PriceInformationFragment : BaseFragment<FragmentPriceInformationBinding>() {

    override fun layoutId() = R.layout.fragment_price_information

    override fun getViewModel(): BaseViewModel? = null

    override fun initOnCreateView() {
        setTitleSubTitle(getString(R.string.pricing_information))
        val priceBreakdown: PriceBreakdown? = requireArguments().getBundle(EXTRA_PRICE_BREAK_DOWN)
            ?.getParcelable(EXTRA_PRICE_BREAK_DOWN)

        priceBreakdown?.convenienceFee?.let {
            bindingView.isConvenienceVisible = priceBreakdown.convenienceFee > 0
        }
        bindingView.priceBreakdown = priceBreakdown
    }

    companion object {
        const val EXTRA_PRICE_BREAK_DOWN = "EXTRA_PRICE_BREAK_DOWN"
    }
}
