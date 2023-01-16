package net.sharetrip.holiday.history.view.infoItem

import androidx.core.os.bundleOf
import androidx.navigation.fragment.findNavController
import com.example.holiday.R
import com.example.holiday.databinding.HolidayInfoFragmentBinding
import net.sharetrip.shared.utils.navigateSafe
import net.sharetrip.shared.view.BaseFragment
import com.sharetrip.base.viewmodel.BaseViewModel
import net.sharetrip.holiday.history.model.BookedPackageInfo
import net.sharetrip.holiday.utils.ARG_HOLIDAY_HISTORY_BOOKED_ITEM
import net.sharetrip.holiday.utils.HOLIDAY_INFO_DETAIL_ARG_ITEM
import net.sharetrip.holiday.utils.HOLIDAY_INFO_DETAIL_ARG_POSITION
import net.sharetrip.holiday.utils.setTitleAndSubtitle

class HolidayInfoFragment : BaseFragment<HolidayInfoFragmentBinding>() {

    override fun layoutId() = R.layout.holiday_info_fragment

    override fun getViewModel(): BaseViewModel? = null

    override fun initOnCreateView() {
        setTitleAndSubtitle(getString(R.string.holiday_information))

        val bookedPackageInfo =
            arguments?.getParcelable<BookedPackageInfo>(ARG_HOLIDAY_HISTORY_BOOKED_ITEM) as BookedPackageInfo

        bindingView.itineraryButton.setOnClickListener {
            val bundle = bundleOf(
                HOLIDAY_INFO_DETAIL_ARG_ITEM to bookedPackageInfo,
                HOLIDAY_INFO_DETAIL_ARG_POSITION to 0
            )
            findNavController().navigateSafe(R.id.action_holidayInfoFragment_to_holidayInfoDetailFragment, bundle)
        }

        bindingView.conditionButton.setOnClickListener {
            val bundle = bundleOf(
                HOLIDAY_INFO_DETAIL_ARG_ITEM to bookedPackageInfo,
                HOLIDAY_INFO_DETAIL_ARG_POSITION to 1
            )
            findNavController().navigateSafe(R.id.action_holidayInfoFragment_to_holidayInfoDetailFragment, bundle)
        }

        bindingView.noteButton.setOnClickListener {
            val bundle = bundleOf(
                HOLIDAY_INFO_DETAIL_ARG_ITEM to bookedPackageInfo,
                HOLIDAY_INFO_DETAIL_ARG_POSITION to 2
            )
            findNavController().navigateSafe(R.id.action_holidayInfoFragment_to_holidayInfoDetailFragment, bundle)
        }

        bindingView.taxButton.setOnClickListener {
            val bundle = bundleOf(
                HOLIDAY_INFO_DETAIL_ARG_ITEM to bookedPackageInfo,
                HOLIDAY_INFO_DETAIL_ARG_POSITION to 3
            )
            findNavController().navigateSafe(R.id.action_holidayInfoFragment_to_holidayInfoDetailFragment, bundle)
        }
    }
}
