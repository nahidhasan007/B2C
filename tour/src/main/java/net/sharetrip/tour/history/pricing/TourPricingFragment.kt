package net.sharetrip.tour.history.pricing

import androidx.fragment.app.viewModels
import net.sharetrip.shared.view.BaseFragment
import com.sharetrip.base.viewmodel.BaseViewModel
import net.sharetrip.tour.R
import net.sharetrip.tour.databinding.FragmentTourPricingInfoBinding
import net.sharetrip.tour.model.TourHistoryItem
import net.sharetrip.tour.utils.setTitleAndSubtitle

class TourPricingFragment : BaseFragment<FragmentTourPricingInfoBinding>() {

    private val viewModel: TourPricingViewModel by viewModels {
        val historyItem =
            arguments?.getParcelable<TourHistoryItem>(ARG_TOUR_HISTORY_ITEM) as TourHistoryItem
        TourPricingVMF(historyItem)
    }

    

    override fun layoutId(): Int = R.layout.fragment_tour_pricing_info

    override fun getViewModel(): BaseViewModel = viewModel

    override fun initOnCreateView() {
        setTitleAndSubtitle(getString(R.string.tour_pricing))

        bindingView.viewModel = viewModel
    }

    

        companion object {
        const val ARG_TOUR_HISTORY_ITEM = "TourInfoScreen.HistoryItem"
    }
}
