package net.sharetrip.tour.booking.highlight

import android.text.Html
import net.sharetrip.shared.view.BaseFragment
import com.sharetrip.base.viewmodel.BaseViewModel
import net.sharetrip.tour.R
import net.sharetrip.tour.databinding.FragmentHighLightTourBinding

class HighLightFragment : BaseFragment<FragmentHighLightTourBinding>() {

    

    override fun layoutId(): Int = R.layout.fragment_high_light_tour

    override fun getViewModel(): BaseViewModel? = null

    override fun initOnCreateView() {
        val title = arguments?.getString(ARG_HIGHLIGHT_TITLE_ARG)
        val detail = arguments?.getString(ARG_HIGHLIGHT_DETAIL_ARG)

        bindingView.textViewTitle.text = title
        bindingView.textViewDescription.text = Html.fromHtml(detail)
    }

    

    companion object {
        const val ARG_HIGHLIGHT_TITLE_ARG = "HighLightScreen.Title.Arg"
        const val ARG_HIGHLIGHT_DETAIL_ARG = "HighLightScreen.Detail.Arg"
    }
}
