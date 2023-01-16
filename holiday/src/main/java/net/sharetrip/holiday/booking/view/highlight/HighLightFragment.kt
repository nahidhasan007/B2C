package net.sharetrip.holiday.booking.view.highlight

import android.text.Html
import com.example.holiday.R
import net.sharetrip.shared.view.BaseFragment
import com.sharetrip.base.viewmodel.BaseViewModel
import net.sharetrip.holiday.booking.HolidayBookingActivity
import net.sharetrip.holiday.utils.ARG_HIGHLIGHT_DETAIL
import net.sharetrip.holiday.utils.ARG_HIGHLIGHT_TITLE
import net.sharetrip.shared.databinding.FragmentHighLightBinding


class HighLightFragment : BaseFragment<FragmentHighLightBinding>() {

    override fun layoutId() = R.layout.fragment_high_light

    override fun getViewModel(): BaseViewModel?  = null

    override fun initOnCreateView() {
        val title = arguments?.getString(ARG_HIGHLIGHT_TITLE)
        val detail = arguments?.getString(ARG_HIGHLIGHT_DETAIL)

        //Setting Toolbar or Actionbar Title
        (activity as HolidayBookingActivity).supportActionBar?.title = title

        bindingView.textViewTitle.text = title
        bindingView.textViewDescription.text = Html.fromHtml(detail)
    }
}
