package net.sharetrip.holiday.history.view.cancelInfo

import androidx.core.text.HtmlCompat
import com.example.holiday.R
import com.example.holiday.databinding.FragmentHolidayHistoryCancelPolicyBinding
import net.sharetrip.shared.view.BaseFragment
import com.sharetrip.base.viewmodel.BaseViewModel
import net.sharetrip.holiday.utils.ARG_CANCEL_POLICY
import net.sharetrip.holiday.utils.setTitleAndSubtitle

class HolidayHistoryCancelPolicyFragment :
    BaseFragment<FragmentHolidayHistoryCancelPolicyBinding>() {

    override fun layoutId() = R.layout.fragment_holiday_history_cancel_policy

    override fun getViewModel(): BaseViewModel? = null

    override fun initOnCreateView() {
        setTitleAndSubtitle(getString(R.string.cancel_policy))

        val cancelPolicy = arguments?.getString(ARG_CANCEL_POLICY)
        bindingView.tvCancelPolicy.text=(HtmlCompat.fromHtml(cancelPolicy.toString(), 0))
    }
    }
