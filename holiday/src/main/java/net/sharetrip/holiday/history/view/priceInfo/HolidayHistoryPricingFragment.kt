package net.sharetrip.holiday.history.view.priceInfo

import android.annotation.SuppressLint
import com.example.holiday.R
import com.example.holiday.databinding.FragmentHolidayPricicingInfoBinding
import net.sharetrip.shared.view.BaseFragment
import com.sharetrip.base.viewmodel.BaseViewModel
import net.sharetrip.holiday.utils.ARG_HOLIDAY_CONV_AMOUNT
import net.sharetrip.holiday.utils.ARG_HOLIDAY_PAID_AMOUNT
import net.sharetrip.holiday.utils.ARG_HOLIDAY_TOTAL_AMOUNT
import net.sharetrip.holiday.utils.setTitleAndSubtitle

class HolidayHistoryPricingFragment : BaseFragment<FragmentHolidayPricicingInfoBinding>() {

    override fun layoutId() = R.layout.fragment_holiday_pricicing_info

    override fun getViewModel(): BaseViewModel? = null

    @SuppressLint("SetTextI18n")
    override fun initOnCreateView() {
        setTitleAndSubtitle(getString(R.string.price_information))

        val totalAmount = arguments?.getString(ARG_HOLIDAY_TOTAL_AMOUNT)
        val paidAmount = arguments?.getString(ARG_HOLIDAY_PAID_AMOUNT)
        val convenienceFee = arguments?.getString(ARG_HOLIDAY_CONV_AMOUNT)
        bindingView.textViewTotalAmount.text = "$totalAmount BDT"
        bindingView.textViewBaseCost.text = "$paidAmount BDT"
        bindingView.textViewConvenienceCost.text = "$convenienceFee BDT"
    }

    
}
