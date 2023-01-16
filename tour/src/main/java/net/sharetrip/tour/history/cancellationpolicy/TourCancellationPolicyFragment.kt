package net.sharetrip.tour.history.cancellationpolicy

import androidx.fragment.app.viewModels
import net.sharetrip.shared.view.BaseFragment
import com.sharetrip.base.viewmodel.BaseViewModel
import net.sharetrip.tour.R
import net.sharetrip.tour.databinding.FragmentTourHtmlBinding
import net.sharetrip.tour.utils.setTitleAndSubtitle

class TourCancellationPolicyFragment : BaseFragment<FragmentTourHtmlBinding>() {

    private val viewModel: TourCancellationPolicyVM by viewModels {
        val htmlString = arguments?.getString(ARG_TOUR_HISTORY_HTML_STRING)
        TourCancellationPolicyVMF(htmlString!!)
    }

    

    override fun layoutId(): Int = R.layout.fragment_tour_html

    override fun getViewModel(): BaseViewModel = viewModel

    override fun initOnCreateView() {
        val title = arguments?.getString(ARG_TOUR_HISTORY_TITLE)
        setTitleAndSubtitle(title!!)

        bindingView.viewModel = viewModel
    }

    

    companion object {
        const val ARG_TOUR_HISTORY_HTML_STRING = "TourHtmlScreen.HistoryItemHtml"
        const val ARG_TOUR_HISTORY_TITLE = "TourHtmlScreen.HistoryItemTitle"
    }
}
