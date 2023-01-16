package net.sharetrip.visa.history.view.price_detail

import androidx.fragment.app.viewModels
import net.sharetrip.shared.view.BaseFragment
import com.sharetrip.base.viewmodel.BaseViewModel
import net.sharetrip.visa.R
import net.sharetrip.visa.databinding.FragmentVisaHistoryPriceDetailsBinding
import net.sharetrip.visa.history.model.VisaProductSnapshot
import net.sharetrip.visa.utils.*
import kotlin.math.ceil

class VisaHistoryPriceDetailsFragment : BaseFragment<FragmentVisaHistoryPriceDetailsBinding>() {
    private val viewModel: VisaHistoryPriceDetailsViewModel by viewModels() {
        val visaProductSnapshot: VisaProductSnapshot =
            arguments?.get(VISA_PRODUCT_SNAPSHOT) as VisaProductSnapshot
        val travellerCount =
            arguments?.get(VISA_TRAVELLER_COUNT) as Int
        val convenienceFee: Double =
            arguments?.getDouble(VISA_CONVENIENCE_FEE, 0.0)!!
        val vatOnConvenienceFee: Double =
            arguments?.getDouble(VISA_VAT_ON_CONVENIENCE_FEE, 0.0)!!
        VisaHistoryPriceDetailsViewModelFactory(
            visaProductSnapshot,
            travellerCount,
            ceil(convenienceFee).toInt(),
            ceil(vatOnConvenienceFee).toInt()
        )
    }

    override fun layoutId() = R.layout.fragment_visa_history_price_details

    override fun getViewModel(): BaseViewModel = viewModel

    override fun initOnCreateView() {
        setTitleAndSubtitle(getString(R.string.pricing_details))
        setToolbarTripCoin()

        bindingView.viewModel = viewModel
        bindingView.lifecycleOwner = viewLifecycleOwner
    }
}