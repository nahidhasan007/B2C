package net.sharetrip.holiday.history.view.information

import androidx.core.text.HtmlCompat
import com.example.holiday.R
import com.example.holiday.databinding.FragmentHolidayInfoDetailBinding
import net.sharetrip.shared.view.BaseFragment
import com.sharetrip.base.viewmodel.BaseViewModel
import net.sharetrip.holiday.history.model.BookedPackageInfo
import net.sharetrip.holiday.utils.HOLIDAY_INFO_DETAIL_ARG_ITEM
import net.sharetrip.holiday.utils.HOLIDAY_INFO_DETAIL_ARG_POSITION
import net.sharetrip.holiday.utils.setTitleAndSubtitle

class HolidayInfoDetailFragment : BaseFragment<FragmentHolidayInfoDetailBinding>() {

    private lateinit var packageInfo: BookedPackageInfo
    var position: Int = 0

    override fun layoutId() = R.layout.fragment_holiday_info_detail

    override fun getViewModel(): BaseViewModel? = null

    override fun initOnCreateView() {
        packageInfo = arguments?.getParcelable<BookedPackageInfo>(HOLIDAY_INFO_DETAIL_ARG_ITEM) as BookedPackageInfo
        position = arguments?.getInt(HOLIDAY_INFO_DETAIL_ARG_POSITION)!!
        showTextOnScreen(position)
    }

    private fun showTextOnScreen(position: Int) {
        when (position) {
            0 -> {
                bindingView.tvDetails.text = HtmlCompat.fromHtml(packageInfo.itinerary, 0)
                setTitleAndSubtitle(getString(R.string.details_itinerary))
            }
            1 -> {
                bindingView.tvDetails.text = HtmlCompat.fromHtml(packageInfo.generalCondition, 0)
                setTitleAndSubtitle(getString(R.string.general_conditions))
            }
            2 -> {
                bindingView.tvDetails.text = HtmlCompat.fromHtml(packageInfo.notes, 0)
                setTitleAndSubtitle(getString(R.string.special_notes))
            }
            3 -> {
                bindingView.tvDetails.text = HtmlCompat.fromHtml(packageInfo.tax, 0)
                setTitleAndSubtitle(getString(R.string.tax))
            }
        }
    }
}
