package net.sharetrip.visa.history.view.cancellation

import androidx.fragment.app.viewModels
import net.sharetrip.shared.view.BaseFragment
import com.sharetrip.base.viewmodel.BaseViewModel
import net.sharetrip.visa.R
import net.sharetrip.visa.databinding.FragmentVisaHistoryCancelPolicyBinding
import net.sharetrip.visa.utils.VISA_HISTORY_CANCELLATION
import net.sharetrip.visa.utils.setTitleAndSubtitle
import net.sharetrip.visa.utils.setToolbarTripCoin

class VisaHistoryCancellationFragment : BaseFragment<FragmentVisaHistoryCancelPolicyBinding>() {
     private val viewModel: VisaHistoryCancellationViewModel by viewModels() {
         val string = requireArguments().getString(VISA_HISTORY_CANCELLATION)
         VisaHistoryCancellationViewModelFactory(string!!)
     }

    override fun layoutId() = R.layout.fragment_visa_history_cancel_policy

    override fun getViewModel(): BaseViewModel = viewModel

    override fun initOnCreateView() {
        setTitleAndSubtitle(getString(R.string.cancellation_policy))
        setToolbarTripCoin()

        bindingView.viewModel = viewModel
        bindingView.lifecycleOwner = viewLifecycleOwner
    }
}