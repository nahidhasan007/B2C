package net.sharetrip.tour.history.information

import androidx.fragment.app.viewModels
import androidx.navigation.fragment.findNavController
import net.sharetrip.shared.event.EventObserver
import net.sharetrip.shared.view.BaseFragment
import com.sharetrip.base.viewmodel.BaseViewModel
import net.sharetrip.tour.R
import net.sharetrip.tour.databinding.TourInfoFragmentBinding
import net.sharetrip.tour.model.TourHistoryItem

class TourInfoFragment : BaseFragment<TourInfoFragmentBinding>() {

    private val viewModel: TourInfoViewModel by viewModels {
        val historyItem =
            arguments?.getParcelable<TourHistoryItem>(ARG_TOUR_HISTORY_ITEM) as TourHistoryItem
        TourInfoVMF(historyItem)
    }

    override fun layoutId(): Int = R.layout.tour_info_fragment

    

    

    override fun getViewModel(): BaseViewModel = viewModel

    override fun initOnCreateView() {
        bindingView.lifecycleOwner = viewLifecycleOwner
        bindingView.viewModel = viewModel

        viewModel.navigateToItinerary.observe(viewLifecycleOwner, EventObserver{
            findNavController().navigate(R.id.action_tourInfoFragment_to_tourCancellationPolicyFragment, it)
        })
    }

    companion object {
        const val ARG_TOUR_HISTORY_ITEM = "TourInfoScreen.HistoryItem"
    }
}
